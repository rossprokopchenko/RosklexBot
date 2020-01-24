package commands;

import main.Rosklex;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

public class Admin extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {


        String[] message = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMessage().getMember();

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.PREFIX) {
            return;
        }

        message[0] = message[0].substring(1, message[0].length());


        if (message[0].equalsIgnoreCase("set")) {
            if (!e.getMessage().getMember().getId().equals("212388900616798218")) {
                e.getChannel().sendMessage("no :)").queue();
                return;
            }

            Member mentionedMember = e.getMessage().getMentionedMembers().get(0);

            try {
                Database.getDb().setColumn(mentionedMember.getId(), message[2], message[3]);
                e.getChannel().sendMessage("Set " + mentionedMember.getUser().getName() + "'s " + message[2] + " to " + message[3]).queue();
            } catch (Exception err) {
                e.getChannel().sendMessage("Something went wrong.").queue();
            }
        }

    }

}
