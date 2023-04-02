package com.rosklex.listeners.events;

import com.rosklex.database.DatabaseManager;
import com.rosklex.database.DatabaseTypes;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class ServerInfoUpdater extends ListenerAdapter {
    private DatabaseManager db;

    public ServerInfoUpdater(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String serverID = event.getGuild().getId();

        boolean existsDb = db.exists(serverID, DatabaseTypes.SERVERS_DB_NAME);

        if (!existsDb) {
            db.newServer(serverID, event.getGuild().getName());

            User rosk = event.getJDA().getUserById("212388900616798218");
            rosk.openPrivateChannel().complete().sendMessage("We're spreading more :0 new server registered: **" + event.getGuild().getName() + "**").queue();
        }

        int requests = Integer.parseInt(db.getColumn(serverID, "requests", DatabaseTypes.SERVERS_DB_NAME));

        db.setColumn(serverID, "requests", "" + (requests + 1), DatabaseTypes.SERVERS_DB_NAME);


    }
}
