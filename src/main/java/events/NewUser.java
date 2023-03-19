package events;

import main.Rosklex;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

public class NewUser extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        if (message[0].charAt(0) == Rosklex.PREFIX) {
            String userId = event.getMessage().getMember().getUser().getId();
            String userName = event.getMessage().getMember().getUser().getName();
            boolean existsDb = Database.getDb().exists(userId);

            if (!existsDb) {
                Database.getDb().newUser(userId, userName);

                // might remove this line of code (so its not annoying)
                event.getChannel().sendMessage("New user added to Database!").queue();
            }
        }

    }
}
