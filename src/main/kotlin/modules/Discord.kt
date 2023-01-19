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
                    val talk = EmojiParser.parseToAliases(message.content).take(237)
                    val mentions = message.mentionedUsers.filter { it.username == "ChatGPT" }

                    val response = OpenAI.complete(talk)


                    if (username != "ChatGPT" && mentions.count() > 0) {
                        Logger.info("Sending to $username...")
                        send(message.channelId, response)
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
