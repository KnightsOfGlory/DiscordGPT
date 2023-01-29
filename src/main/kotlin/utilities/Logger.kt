package utilities

import dev.kord.core.entity.Message
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    private fun stamp(): String = LocalDateTime.now().format(formatter)

    private fun log(level: String, message: String) {
        println("[${stamp()}] [$level] $message")
    }

    fun debug(message: String) { log("DEBUG", message) }
    fun info(message: String) { log("INFO", message) }
    fun error(message: String) { log("ERROR", message) }

    fun discord(message: Message) {
        suspend {
            val channel = message.channel.asChannel().data.name.value!!
            val user = message.author!!.username
            val talk = message.content
            info("[DISCORD] #${channel} @$user $talk")
        }
    }
}
