package com.rosklex.listeners.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;

public class NewUser extends ListenerAdapter {
    private DatabaseManager db;

    public NewUser(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        String userId = event.getMessage().getMember().getUser().getId();
        String userName = event.getMessage().getMember().getUser().getName();
        boolean existsDb = db.exists(userId, "scores");

        if (!existsDb) {
            db.newUser(userId, userName);


            event.getMessage().reply("New user added to Database!").queue();
        }


    }
}
