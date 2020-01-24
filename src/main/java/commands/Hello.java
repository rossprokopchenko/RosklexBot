package commands;

import main.Rosklex;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Hello extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");

        if (message[0].charAt(0) != Rosklex.PREFIX) return;
        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("hello")) {
            e.getChannel().sendMessage("hello!").queue();
        }
    }
}
