package events;

import main.Rosklex;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

public class NewUser extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMessage().getMember().getUser().isBot()) return;

        String[] message = e.getMessage().getContentRaw().split(" ");

        if (message[0].charAt(0) == Rosklex.PREFIX) {
            String userId = e.getMessage().getMember().getUser().getId();
            String userName = e.getMessage().getMember().getUser().getName();
            boolean existsDb = Database.getDb().exists(userId);

            if (!existsDb) {
                Database.getDb().newUser(userId, userName);

                // might remove this line of code (so its not annoying)
                e.getChannel().sendMessage("New user added to Database!").queue();
            }
        }

    }
}
