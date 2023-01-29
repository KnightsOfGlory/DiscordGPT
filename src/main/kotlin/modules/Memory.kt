package modules

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import org.apache.commons.collections4.queue.CircularFifoQueue
import utilities.Logger

object Memory {

    private val messages = mutableMapOf<Snowflake, CircularFifoQueue<Message>>()

    fun remember(channel: Snowflake, message: Message) {
        Logger.debug("Remembering $message in $channel")
        ensureExists(channel)
        messages[channel]?.add(message)
    }

    fun recall(channel: Snowflake): String {
        val conversation = messages[channel]?.joinToString("\n") { formatMessage(it.author!!, it.content).take(35) } ?: ""

        return  "Following is a conversation with a sassy ChatGPT assistant and some humans.\n\n" +
                conversation + "\nChatGPT:"
    }

    fun participants(channel: Snowflake): List<String> {
        return messages[channel]
            ?.map { formatParticipant(it.author!!) }
            ?.toList() ?: listOf()
    }

    private fun ensureExists(snowflake: Snowflake ) {
        if (!messages.containsKey(snowflake)) {
            Logger.debug("Creating memory for $snowflake")
            messages[snowflake] = CircularFifoQueue(3)
        }
    }

    private fun formatParticipant(author: User): String {
        return author.username
    }

    private fun formatMessage(author: User, message: String): String {
        val cleaned = message
            .replace("\n", " ")
            .replace("<@\\d+>:".toRegex(), "")
            .replace("<@\\d+>!".toRegex(), "")
        return "${author.username}: $cleaned"
    }
}
