package com.rosklex.listeners.events;


import com.rosklex.boot.Rosklex;
import com.rosklex.database.DatabaseManager;
import com.rosklex.database.DatabaseTypes;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;

import java.util.EventListener;
import java.util.List;

public class RosklexMessage extends ListenerAdapter {
    private static DatabaseManager db;

    public RosklexMessage(DatabaseManager db) {
        this.db = db;
    }

    public static boolean isRosklexMessage(Message message) {
        List<Message.Attachment> attachments = message.getAttachments();

        if (attachments == null) return true;
        if (!attachments.isEmpty()) return false;
        if (message.getMember() == null) return false;

        Member member = message.getMember();
        String[] messageContent = message.getContentRaw().split(" ");

        String serverID = message.getGuild().getId();

        boolean existsDb = db.exists(serverID, DatabaseTypes.SERVERS_DB_NAME);
        char prefix = Rosklex.getPrefix();

        if (existsDb) {
            prefix = db.getColumn(serverID, "prefix", DatabaseTypes.SERVERS_DB_NAME).charAt(0);
        }

        if (StringUtils.isEmpty("" + messageContent[0])) return false;

        if (member.getUser().isBot() || messageContent[0].charAt(0) != prefix) {
            return false;
        }

        return true;
    }
}
