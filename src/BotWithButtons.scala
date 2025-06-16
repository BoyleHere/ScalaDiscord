import Bot.{MessageListener, token}
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api._
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.GatewayIntent
import scala.jdk.CollectionConverters._

import scala.collection.mutable

object BotWithButtons extends App {
  val dotenv = Dotenv.load()
  val token = dotenv.get("DISCORD_TOKEN")

  val jda = JDABuilder.createDefault(token)
    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
    .addEventListeners(new MessageListener, new ButtonHandler)
    .build()

//  jda.upsertCommand(Commands.slash("ask", "Ask a yes/no question")).queue()
  jda.awaitReady()

//  jda.retrieveCommands().queue { commands =>
//    commands.asScala.foreach { cmd =>
//      if (cmd.getName == "ask" && cmd.getOptions.isEmpty) {
//        println(s"Deleting global command: ${cmd.getName}")
//        jda.deleteCommandById(cmd.getId).queue()
//      }
//    }
//  }

  val guild = jda.getGuildById("859766481117773874")
  if (guild != null) {
    guild.upsertCommand("ask", "Ask a yes/no question")
      .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "question", "The question to ask", true)
      .queue()
  }
  private class MessageListener extends ListenerAdapter {
    // To track which messages are questions
    private val questionMessages = mutable.Map[String, User]()

    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
      if (event.getAuthor.isBot) return

      val msg = event.getMessage.getContentRaw
      //      val user = event.getAuthor

      if (msg.equalsIgnoreCase("hi")) {
        event.getChannel.sendMessage("hi").queue()
      }

      if (msg.equalsIgnoreCase("?ask")) {
        val yesButton = Button.success("yes_btn", "✅ Yes")
        val noButton = Button.danger("no_btn", "❌ No")

        event.getChannel
          .sendMessage("Do you agree?")
          .addActionRow(yesButton, noButton)
          .queue()
      }
    }
  }
  class ButtonHandler extends ListenerAdapter {

//    private val respondedUsers = mutable.Set[String]()
  private val respondedMap = mutable.Map[String, mutable.Set[String]]()

    override def onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = {
      if (event.getName == "ask") {
        val question = event.getOption("question").getAsString
        event.reply(s"$question")
          .addActionRow(
            Button.success("yes_btn", "✅ Yes"),
            Button.danger("no_btn", "❌ No")
          )
          .setEphemeral(false)
          .queue({ hook =>
            hook.retrieveOriginal().queue({message =>
              respondedMap(message.getId) = mutable.Set[String]()
            })
          })
      }
    }

    override def onButtonInteraction(event: ButtonInteractionEvent): Unit = {
      val messageId = event.getMessageId
      val userId = event.getUser.getId
      val respondedUsers = respondedMap.getOrElseUpdate(messageId, mutable.Set[String]())
      if (respondedUsers.contains(userId)) {
        event.reply("⛔ You’ve already responded.").setEphemeral(true).queue()
        return
      }

      respondedUsers += userId

      val response = event.getButton.getId match {
        case "yes_btn" => "✅ You selected: Yes"
        case "no_btn" => "❌ You selected: No"
        case _ => "⚠️ Unknown button!"
      }

      event.reply(response).setEphemeral(true).queue()
    }
  }
}
