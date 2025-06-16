# Scala Discord Bot – Reactions & Buttons

So I made a Discord bot using Scala and JDA. It can do two things:

1. Ask a question using reactions ✅ ❌
2. Ask a question using buttons ✅ ❌

## Setup

- Uses Scala
- Uses JDA (Java Discord API)
- Uses dotenv to store the bot token
- You’ll need a `.env` file with:

DISCORD_TOKEN=your_token_here


## Features

### 1. Reaction-Based Ask Bot (`Bot.scala`)
- When someone types `ask`, the bot sends a message like:  
*"Do you like Scala?"*
- It adds ✅ and ❌ reactions.
- Only the person who asked can react and get a reply.

### 2. Slash Command with Buttons (`BotWithButtons.scala`)
- Slash command: `/ask question: "your question here"`
- Sends a message with two buttons: Yes ✅ and No ❌
- Users can click once — after that, if they try again, it says “⛔ You’ve already responded.”
- Each new question resets the list of responders.

## Notes

- Works only in guild (server), not in DMs.
- Slash command is registered per guild (not global).
- Keeps things ephemeral when needed (or not, based on use).
- No fancy database. Just a `mutable.Map` to track things.

## To Run

Make sure you've got JDK 17+ and sbt.

```bash
sbt run
```
Future Ideas 
Store responses somewhere (like a DB)

Timeout old questions

Add more types of buttons or polls

That’s it. It works. 👍

