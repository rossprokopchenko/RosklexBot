package com.rosklex.listeners.commands;

import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class Invite extends ListenerAdapter {

    private ArrayList<Button> buttons;

    public Invite() {
        buttons = new ArrayList<>();
        buttons.add(Button.link("https://dsc.gg/rosklex", "Invite link"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if(message[0].equalsIgnoreCase("invite")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);
            eb.setTimestamp(new Date().toInstant());
            eb.setTitle("\uD83E\uDD16 Rosklex Invite Link");

            eb.setDescription("Invite Rosklex to a server with the following link:\n\n" +
                    "https://dsc.gg/rosklex");

            event.getMessage().replyEmbeds(eb.build())
                    .addActionRow(buttons)
                    .queue();
        }
    }
}
