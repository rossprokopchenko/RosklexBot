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
import java.util.Date;

public class Info extends ListenerAdapter {

    private DatabaseManager db;

    public Info(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if(message[0].equalsIgnoreCase("info")){
            EmbedBuilder eb = new EmbedBuilder();

            loadEmbedContent(eb, event);

            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }

    private void loadEmbedContent(EmbedBuilder eb, MessageReceivedEvent event) {
        eb.setColor(Color.GREEN);
        eb.setTimestamp(new Date().toInstant());
        eb.setTitle("\uD83E\uDD16 Basic Info");

        String prefix = db.getColumn(event.getGuild().getId(), "prefix", DatabaseTypes.SERVERS_DB_NAME);

        eb.addField("**About**",
                "Hi! Mainly, this bot lets you play time-based dungeons. " +
                        "After completing dungeons, you get rewards. These rewards are accounted to your **profile**! " +
                        "With those rewards, you can shop at the store, to improve the overall look of your profile and " +
                        "you characteristics.",
                false);
        eb.addField("**Dungeons**", "To find out more about dungeons, please type **" + prefix + "dungeons**", false);
        eb.addField("**Mine**", "If you decide to buy a pickaxe from the store, " +
                "you may enter the mines by typing **" + prefix + "mine**.", false);
        eb.addField("***More***", "If you wish to find out more possibilities, please type **" + prefix + "help**.", false);
    }

}
