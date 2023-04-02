package com.rosklex.listeners.commands;

import com.rosklex.boot.Rosklex;
import com.rosklex.database.DatabaseManager;
import com.rosklex.database.DatabaseTypes;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.Date;
import java.util.List;

@Slf4j
public class Prefix extends ListenerAdapter {
    private DatabaseManager db;

    public Prefix(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        if (!attachments.isEmpty()) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        if (StringUtils.isEmpty("" + message[0])) return;

        if (member == null) return;

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.getPrefix()) {
            return;
        }

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("prefix")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            boolean existsDb = db.exists(event.getGuild().getId(), DatabaseTypes.SERVERS_DB_NAME);

            if (!existsDb) {
                db.newServer(event.getGuild().getId(), event.getGuild().getName());

                User rosk = event.getJDA().getUserById("212388900616798218");
                rosk.openPrivateChannel().complete().sendMessage("We're spreading more :0 new server registered: **" + event.getGuild().getName() + "**").queue();
            }

            String serverName = db.getColumn(event.getGuild().getId(), "name", DatabaseTypes.SERVERS_DB_NAME);
            String serverPrefix = db.getColumn(event.getGuild().getId(), "prefix", DatabaseTypes.SERVERS_DB_NAME);

            String titleStr = String.format("**[%s]** Rosklex Prefix", serverName);
            String descriptionStr = String.format("This server's prefix is **%s**.\n" +
                    "If you need to change it, ask a server administrator to use **%sprefix set [prefix]**\n" +
                    "To see what this bot does type **%sinfo**", serverPrefix, Rosklex.getPrefix(), serverPrefix);

            if (message.length > 1) {
                if (message[1].equalsIgnoreCase("set")) {
                    boolean perms = member.hasPermission(Permission.MANAGE_SERVER);

                    if (message.length == 3) {
                        if (message[2].length() > 1) return;

                        char newPrefix = message[2].charAt(0);

                        descriptionStr = String.format("You successfully set the Rosklex prefix to %s!", newPrefix);

                        if (!perms) {
                            descriptionStr = String.format("You do not have permissions to manage this server.");
                        } else {
                            db.setColumn(event.getGuild().getId(), "prefix", "" + newPrefix, DatabaseTypes.SERVERS_DB_NAME);
                        }
                    }
                }
            }

            eb.setColor(Color.RED);
            eb.setTitle(titleStr);
            eb.setDescription(descriptionStr);

            eb.setTimestamp(date.toInstant());

            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }
}
