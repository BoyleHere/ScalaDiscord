import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api._
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button

import scala.collection.mutable

object Bot extends App {

  val dotenv = Dotenv.load()
  val token = dotenv.get("DISCORD_TOKEN")
  val jda = JDABuilder.createDefault(token)
    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
    .addEventListeners(new MessageListener)
    .build()

  private class MessageListener extends ListenerAdapter {
    // To track which messages are questions
    private val questionMessages = mutable.Map[String, User]()

    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
      if (event.getAuthor.isBot) return

      val msg = event.getMessage.getContentRaw.toLowerCase
      val user = event.getAuthor

      if (msg == "ask") {
        event.getChannel.sendMessage("Do you like Scala?")
          .queue { message =>
            message.addReaction(Emoji.fromUnicode("✅")).queue()
            message.addReaction(Emoji.fromUnicode("❌")).queue()
            questionMessages.put(message.getId, user) // track who asked
          }
      }
    }

    override def onMessageReactionAdd(event: MessageReactionAddEvent): Unit = {
      if (event.getUser.isBot) return

      val messageId = event.getMessageId
      val user = event.getUser

      if (questionMessages.contains(messageId) && user == questionMessages(messageId)) {
        event.getReaction.getEmoji.getName match {
          case "✅" =>
            event.getChannel.sendMessage(s"${user.getName} likes Scala! 🎉").queue()
            questionMessages.remove(messageId)

          case "❌" =>
            event.getChannel.sendMessage(s"${user.getName} does not like Scala 😢").queue()
            questionMessages.remove(messageId)

          case _ => // ignore other reactions
        }
      }
    }
  }
}
