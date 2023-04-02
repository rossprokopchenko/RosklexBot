package com.rosklex.listeners.commands;

import com.rosklex.database.DatabaseManager;
import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Stats extends ListenerAdapter {
    private DatabaseManager db;

    public Stats(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("stats") || message[0].equalsIgnoreCase("statistics")) {

        }
    }
}
