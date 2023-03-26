package listeners.commands;

import listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import database.DatabaseManager;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Daily extends ListenerAdapter {
    private DatabaseManager db;
    private Profile profile;

    public Daily(DatabaseManager db, Profile profile) {
        this.db = db;
        this.profile = profile;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("daily")) {
            long lastDaily = profile.getMemberDaily(member);
            Calendar now = Calendar.getInstance();
            Date date = new Date();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.BLUE);
            eb.setTimestamp(date.toInstant());
            eb.setFooter("Daily Prompt", member.getUser().getAvatarUrl());

            int dailyExp = 100 + profile.getMemberLevel(member) * 7;
            int dailyCoins = 150 + profile.getMemberLevel(member) * 20;

            if (message.length == 2 && message[1].equalsIgnoreCase("help")) {
                eb.setTitle("\uD83D\uDD65 Daily Help");
                eb.setDescription(" The daily reward grants you " + dailyExp + " exp and " + dailyCoins + " coins. " +
                        "Once you claim your daily, your next daily will be available at 12 AM Eastern Standard Time");
                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            } else if (message.length > 1) {
                event.getChannel().sendMessage("Too many arguments").queue();
                return;
            }

            if (dailyAvailable(member)) {
                Calendar tomorrow = Calendar.getInstance();

                tomorrow.set(Calendar.HOUR_OF_DAY, 0);
                tomorrow.set(Calendar.MINUTE, 0);
                tomorrow.set(Calendar.SECOND, 0);
                tomorrow.set(Calendar.MILLISECOND, 0);
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);

                int newExp = profile.getMemberExp(member) + dailyExp;
                int newCoins = profile.getMemberCoins(member) + dailyCoins;

                db.setColumn(member.getId(), "lastDaily", "" + tomorrow.getTimeInMillis());
                db.setColumn(member.getId(), "exp", "" + newExp);
                db.setColumn(member.getId(), "coins", "" + newCoins);

                eb.setTitle("\uD83D\uDD65 Daily Reward Claimed");
                eb.setDescription("You claimed your daily! Added \uD83C\uDF1F **" + dailyExp + "** and \uD83D\uDCB0 **" + dailyCoins + "** to your account.");
            } else {

                long millisLeft = lastDaily - now.getTimeInMillis();
                long hoursLeft = millisLeft / (60 * 60 * 1000);
                long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                long secondsLeft = (millisLeft / 1000) % 60;

                String hours = Long.toString(hoursLeft);
                String minutes = Long.toString(minutesLeft);
                String seconds = Long.toString(secondsLeft);

                eb.setTitle("\uD83D\uDD65 Daily Reward Error");
                eb.setDescription("You already claimed your daily today. Your next daily will be available in " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds.");
            }

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public boolean dailyAvailable(Member member) {
        long lastDaily = profile.getMemberDaily(member);

        Calendar now = Calendar.getInstance();

        if (lastDaily < now.getTimeInMillis())
            return true;
        else
            return false;

    }
}
