package commands;

import main.Rosklex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Daily extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMessage().getMember();

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.PREFIX) {
            return;
        }

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("daily")) {
            long lastDaily = Profile.getMemberDaily(member);
            Calendar now = Calendar.getInstance();
            Date date = new Date();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);
            eb.setTimestamp(date.toInstant());
            eb.setFooter("Daily Prompt", member.getUser().getAvatarUrl());

            int dailyExp = 100 + Profile.getMemberLevel(member) * 7;
            int dailyCoins = 150 + Profile.getMemberLevel(member) * 10;

            if (message.length == 2 && message[1].equalsIgnoreCase("help")) {
                eb.setTitle("\uD83D\uDD65 Daily Help");
                eb.setDescription(" The daily reward grants you " + dailyExp + " exp and " + dailyCoins + " coins. " +
                        "Once you claim your daily, your next daily will be available at 12 AM Eastern Standard Time");
                e.getChannel().sendMessage(eb.build()).queue();
                return;
            } else if (message.length > 1) {
                e.getChannel().sendMessage("Too many arguments").queue();
                return;
            }

            if (dailyAvailable(member)) {
                Calendar tomorrow = Calendar.getInstance();

                tomorrow.set(Calendar.HOUR_OF_DAY, 0);
                tomorrow.set(Calendar.MINUTE, 0);
                tomorrow.set(Calendar.SECOND, 0);
                tomorrow.set(Calendar.MILLISECOND, 0);
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);

                int newExp = Profile.getMemberExp(member) + dailyExp;
                int newCoins = Profile.getMemberCoins(member) + dailyCoins;

                Database.getDb().setColumn(member.getId(), "lastDaily", "" + tomorrow.getTimeInMillis());
                Database.getDb().setColumn(member.getId(), "exp", "" + newExp);
                Database.getDb().setColumn(member.getId(), "coins", "" + newCoins);

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

            e.getChannel().sendMessage(eb.build()).queue();
        }
    }

    public static boolean dailyAvailable(Member member) {
        long lastDaily = Profile.getMemberDaily(member);

        Calendar now = Calendar.getInstance();

        if (lastDaily < now.getTimeInMillis())
            return true;
        else
            return false;

    }
}
