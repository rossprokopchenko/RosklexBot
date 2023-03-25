package events;

import commands.Profile;
import main.Rosklex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

public class LevelListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        message[0] = message[0].substring(1, message[0].length());

        Member member = event.getMessage().getMember();
        int memberExp = Profile.getMemberExp(member);
        int memberLevel = Profile.getMemberLevel(member);

        int levelUp = 50 * (int) Math.pow(Profile.getMemberLevel(member) - 2, 2) + 150 * (Profile.getMemberLevel(member) - 2) + 300;

        if (memberExp >= levelUp) {
            while (memberExp >= levelUp) {
                memberLevel++;
                memberExp -= levelUp;
            }

            Database.getDb().setColumn(member.getId(), "level", "" + memberLevel);
            Database.getDb().setColumn(member.getId(), "exp", "" + memberExp);

            event.getChannel().sendMessage(member.getAsMention() + " leveled up to **level " + memberLevel + "**! Congratulations!").queue();
        }

        if (message[0].equalsIgnoreCase("level")) {

            double progress = round(((double) memberExp / levelUp) * 100, 2);

            int boxes = (int) progress / 5;
            int whiteBoxes = (100 - (int) progress) / 5;
            String xpBar = "";

            for (int i = 0; i < boxes; i++) {
                xpBar += "●";
            }

            for (int i = 0; i < whiteBoxes; i++) {
                xpBar += "○";
            }

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            if (message.length == 2 && message[1].equalsIgnoreCase("help")) {
                String[] totalExp = new String[41];
                String levelString = "*Level 2-4* ▫ ";

                for (int i = 1; i < totalExp.length - 1; i++) {
                    totalExp[i] = "" + (50 * (int) Math.pow(i - 2, 2) + 150 * (i - 2) + 300);

                    double amount = Double.parseDouble(totalExp[i]);
                    DecimalFormat formatter = new DecimalFormat("#,###");

                    if (i == totalExp.length - 2) {
                        levelString += "**" + (i + 1) + "** (" + formatter.format(amount) + ")";
                    } else {
                        levelString += "**" + (i + 1) + "** (" + formatter.format(amount) + "), ";

                        if ((i + 1) % 4 == 0) {
                            levelString += "\n*Level " + (i + 2) + "-" + (i + 5) + "* ▫ ";
                        }
                    }


                }

                eb.setTitle("\uD83D\uDCC8 Level Ladder");
                eb.addField("Formatted in \"Level (required experience)\"",
                        levelString, false);

                eb.addField("", "*Formula by based god Kevvol*", false);

                eb.setColor(Color.RED);
                eb.setTimestamp(date.toInstant());
                eb.setFooter("Level Ladder", member.getUser().getAvatarUrl());

                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            } else if (message.length > 1) {
                event.getChannel().sendMessage("Too many arguments").queue();
                return;

            }

            eb.setTitle("You are " + progress + "% there on the way to **level " + (memberLevel + 1) + "**!");
            eb.setDescription("\uD83C\uDF1F Experience: " + memberExp + "/" + levelUp
                    + "\n\n[ " + xpBar + " ]");
            eb.addField("", "*enter **level help** to see the level ladder*", false);

            eb.setColor(Color.RED);
            eb.setTimestamp(date.toInstant());
            eb.setFooter(member.getUser().getName() + "'s level stats", member.getUser().getAvatarUrl());

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
