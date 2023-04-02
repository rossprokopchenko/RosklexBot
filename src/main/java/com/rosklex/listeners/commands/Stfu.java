package com.rosklex.listeners.commands;

import com.rosklex.database.DatabaseManager;
import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Stfu extends ListenerAdapter {
    public Stfu() {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("stfu")) {
            event.getMessage().reply("no u stfu lol " + event.getMember().getAsMention()).queue();
        }
    }
}
