package com.rosklex.listeners.commands;

import com.rosklex.boot.Rosklex;
import com.rosklex.database.DatabaseManager;
import com.rosklex.database.DatabaseTypes;
import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Help extends ListenerAdapter {

    private DatabaseManager db;

    public Help(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("help")) {
            Properties properties = new Properties();

            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream("rosklex.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String version = properties.getProperty("version");
            String prefix = db.getColumn(event.getGuild().getId(), "prefix", DatabaseTypes.SERVERS_DB_NAME);

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.MAGENTA);
            eb.setTitle("\uD83E\uDD16 Rosklex v" + version + " Bot Commands");
            eb.addField("Prefix: " + prefix,
                    "**prefix** : see / manage this server's prefix for this bot\n" +
                            "**info** : all you have to know about the bot\n" +
                            "**profile** *[user]* : view your or a mentioned user's profile\n" +
                            "**inventory** *[user]* : view your or a mentioned user's inventory\n" +
                            "**level** : see your progress to the next level\n" +
                            "**daily** : claim your daily bonus\n" +
                            "**store** : access the item shop\n" +
                            "**dungeon help** : access the dungeon help menu\n" +
                            "**mine** : test your luck in the mine (you must own a pickaxe)\n" +
                            "**sell** : sell your mine loot (ex. **sell mine all**)\n" +
                            "**top** *[page 1-3]*: see the leaderboard of current registered users\n" +
                            "**invite** : invite this bot to a server", false);
            eb.setTimestamp(date.toInstant());

            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }

}
