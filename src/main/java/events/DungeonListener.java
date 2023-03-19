package events;

import commands.Dungeon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import java.awt.*;
import java.util.Date;

public class DungeonListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();



        message[0] = message[0].substring(1, message[0].length());

        int dungeonNotifier = Integer.parseInt(Database.getDb().getColumn(member.getId(), "dNotifier"));

        if (!Dungeon.inDungeon(member) && dungeonNotifier == 1) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            eb.setTitle("⚔ Dungeon Help Prompt");
            eb.setDescription(member.getUser().getAsMention() + ", your **" + Database.getDb().getColumn(member.getId(), "dDiff") +
                    "** dungeon run is over. Use **dungeon claim** to see the results.");
            Database.getDb().setColumn(member.getId(), "dNotifier", "0");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
