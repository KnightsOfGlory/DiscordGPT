import modules.Discord
import modules.Health
import utilities.Logger

fun main(args: Array<String>) {

    Logger.info("Starting DiscordGPT...")

    Logger.info("Connecting to Discord...")
    Discord.connect()

    Logger.info("Starting health monitor...")
    Health.start()
}
