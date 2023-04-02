package com.rosklex.listeners.commands;

import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;

public class Admin extends ListenerAdapter {
    private DatabaseManager db;

    public Admin(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if(message[0].equalsIgnoreCase("set")){
            if(!event.getMessage().getMember().getId().equals("212388900616798218")){
                event.getChannel().sendMessage("no :)").queue();
                return;
            }

            Member mentionedMember =  event.getMessage().getMentions().getMembers().get(0);

            try{
                db.setColumn(mentionedMember.getId(), message[2], message[3], "scores");
                event.getChannel().sendMessage("Set " + mentionedMember.getUser().getName() + "'s " + message[2] + " to " + message[3]).queue();
            } catch(Exception err){
                event.getChannel().sendMessage("Something went wrong. " + err.getMessage()).queue();
            }
        } else if (message[0].equalsIgnoreCase("get")) {
            Member mentionedMember =  event.getMessage().getMentions().getMembers().get(0);

            try{
                String result = db.getColumn(mentionedMember.getId(), message[2], "scores");
                event.getChannel().sendMessage("Got " + mentionedMember.getUser().getName() + "'s " + message[2] + ", result = " + result).queue();
            } catch(Exception err){
                event.getChannel().sendMessage("Something went wrong. " + err.getMessage()).queue();
            }
        }

    }

}
