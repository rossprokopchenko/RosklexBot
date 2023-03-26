package listeners.commands;

import listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Date;

public class Help extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("help")) {
            Date date = new Date();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.MAGENTA);
            eb.setTitle("\uD83E\uDD16 Rosklex Bot Commands");
            eb.addField("Standard Prefix: ❗",
                    "**profile** *[user]* : view your or a mentioned user's profile\n" +
                            "**inventory** *[user]* : view your or a mentioned user's inventory\n" +
                            "**level** : see your progress to the next level\n" +
                            "**daily** : claim your daily bonus\n" +
                            "**store** : access the item shop\n" +
                            "**dungeon help** : access the dungeon help menu\n" +
                            "**mine** : test your luck in the mine (you must own a pickaxe)\n" +
                            "**sell** : sell your mine loot (ex. **sell mine all**)\n" +
                            "**top** *[page 1-3]*: see the leaderboard of current registered users\n" +
                            "**info** : all you have to know about the bot", false);
            eb.setTimestamp(date.toInstant());

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }

    }
}
