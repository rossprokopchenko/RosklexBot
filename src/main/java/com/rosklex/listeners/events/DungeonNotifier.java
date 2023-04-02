package com.rosklex.listeners.events;

import com.rosklex.listeners.commands.Dungeon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;

import java.awt.*;
import java.util.Date;

public class DungeonNotifier extends ListenerAdapter {
    private DatabaseManager db;
    private Dungeon dungeon;

    public DungeonNotifier(DatabaseManager db, Dungeon dungeon) {
        this.db = db;
        this.dungeon = dungeon;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("dungeon")) {
            if (message.length == 1) {
                return;
            }

            if (message[1].equalsIgnoreCase("claim") || message[1].equalsIgnoreCase("enter")) {
                return;
            }
        }

        int dungeonNotifier = Integer.parseInt(db.getColumn(member.getId(), "dNotifier", "scores"));

        if (!dungeon.inDungeon(member) && dungeonNotifier == 1) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            eb.setTitle("⚔ Dungeon Help Prompt");
            eb.setDescription(member.getUser().getAsMention() + ", your **" + db.getColumn(member.getId(), "dDiff", "scores") +
                    "** dungeon run is over. Use **dungeon claim** to see the results.");
            db.setColumn(member.getId(), "dNotifier", "0", "scores");
            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }
}
