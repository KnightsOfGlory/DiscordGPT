package modules

import com.vdurmont.emoji.EmojiParser
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import utilities.Logger

object Discord {

    private val token = System.getenv("DISCORD_TOKEN")

    private lateinit var kord: Kord
    private var rest = RestClient(token)

    class Listener: Runnable {
        override fun run() {
            runBlocking {
                kord = Kord(token)

                Logger.info("Subscribing to Discord messages...")
                kord.on<MessageCreateEvent> {
                    Logger.discord(message)

                    val username = message.author?.username
//                    val talk = EmojiParser.parseToAliases(message.content).take(237)
                    val mentions = message.mentionedUsers.filter { it.username == "ChatGPT" }

                    Memory.remember(message.channelId, message)

                    if (username != "ChatGPT" && mentions.count() > 0) {
                        val conversation = Memory.recall(message.channelId)
                        val participants = Memory.participants(message.channelId)

                        Logger.debug("Conversation: $conversation")
                        Logger.debug("Participants: $participants")

                        Thread.sleep(1000)
                        val response = OpenAI.complete(conversation, participants)
                        Logger.debug("Response length: ${response.length}")
                        if (response.isNotEmpty()) {
                            Logger.info("Response: $response")
                            val author = "<@${message.author?.id}>"

                            Logger.debug("Sending to $username...")
                            send(message.channelId, "$author: $response")
                        } else {
                            Logger.debug("Empty response from OpenAI")
                        }
                    } else {
                        Logger.debug("Username: $username, mentions: ${mentions.count()}")
                    }
                }

                Logger.info("Logging in to Discord...")
                kord.login {
                    @OptIn(PrivilegedIntent::class)
                    intents += Intent.MessageContent
                }
            }
        }
    }

    fun connect() {
        val listener = Listener()
        val thread = Thread(listener)

        thread.start()
    }

    suspend fun send(channel: Snowflake, message: String) {
        rest.channel.createMessage(channel) {
            content = message
        }
    }
}
