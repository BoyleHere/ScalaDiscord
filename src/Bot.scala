import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import io.github.cdimascio.dotenv.Dotenv

object Bot extends App {

  val dotenv = Dotenv.load()
  val token = dotenv.get("DISCORD_TOKEN")
  val jda = JDABuilder.createDefault(token)
    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
    .addEventListeners(new MessageListener)
    .build()

  private class MessageListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
      if (event.getAuthor.isBot) return

      val msg = event.getMessage.getContentRaw
      if (msg.equalsIgnoreCase("hi")) {
        event.getChannel.sendMessage("hi").queue()
      }
    }
  }
}
