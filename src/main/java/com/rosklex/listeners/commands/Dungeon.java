package com.rosklex.listeners.commands;

import com.rosklex.listeners.events.LevelUpNotifier;
import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Dungeon extends ListenerAdapter {

    private final int easyLow = 25;
    private final int easyHigh = 50;
    private final int easyCoins = 80;
    private final int easyAttackReq = 0;
    private final int easyDefenceReq = 0;
    private final int easyLength = 30;

    private final int normalLow = 100;
    private final int normalHigh = 200;
    private final int normalCoins = 200;
    private final int normalAttackReq = 5;
    private final int normalDefenceReq = 0;
    private final int normalLength = 120;

    private final int challengingLow = 500;
    private final int challengingHigh = 1000;
    private final int challengingCoins = 500;
    private final int challengingAttackReq = 15;
    private final int challengingDefenceReq = 10;
    private final int challengingLength = 360;

    private final int extremeLow = 1000;
    private final int extremeHigh = 2000;
    private final int extremeCoins = 800;
    private final int extremeAttackReq = 50;
    private final int extremeDefenceReq = 25;
    private final int extremeLength = 720;

    private final int legendaryLow = 5000;
    private final int legendaryHigh = 7500;
    private final int legendaryCoins = 2000;
    private final int legendaryAttackReq = 200;
    private final int legendaryDefenceReq = 100;
    private final int legendaryLength = 2180;

    private int swiftness;

    private int easyMinutes;
    private int normalHours;
    private int normalMinutes;

    private int challengingHours;
    private int challengingMinutes;

    private int extremeHours;
    private int extremeMinutes;

    private int legendaryHours;
    private int legendaryMinutes;

    private DatabaseManager db;
    private Profile profile;
    private LevelUpNotifier levelUpNotifier;
    private ArrayList<Button> buttons;

    private Member messageMember;
    private Message message;

    public Dungeon(DatabaseManager db, Profile profile, LevelUpNotifier levelUpNotifier) {
        this.db = db;
        this.profile = profile;
        this.levelUpNotifier = levelUpNotifier;

        buttons = new ArrayList<>();
        buttons.add(Button.secondary("easyButton", Emoji.fromUnicode("\uD83D\uDFE2")));
        buttons.add(Button.secondary("normalButton", Emoji.fromUnicode("\uD83D\uDFE0")));
        buttons.add(Button.secondary("challengingButton", Emoji.fromUnicode("\uD83D\uDD34")));
        buttons.add(Button.secondary("extremeButton", Emoji.fromUnicode("☢")));
        buttons.add(Button.secondary("legendaryButton", Emoji.fromUnicode("\uD83C\uDFF5")));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        messageMember = event.getMessage().getMember();
        this.message = event.getMessage();

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("dungeon") || message[0].equalsIgnoreCase("dungeons")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            String diff = db.getColumn(messageMember.getId(), "dDiff", "scores");

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            swiftness = profile.getMemberSwiftness(messageMember);
            easyMinutes = (easyLength - swiftness);
            normalHours = (normalLength - swiftness * 4) / 60;
            normalMinutes = (normalLength - swiftness * 4) - (60 * normalHours);
            challengingHours = (challengingLength - swiftness * 12) / 60;
            challengingMinutes = (challengingLength - swiftness * 12) - (60 * challengingHours);
            extremeHours = (extremeLength - swiftness * 24) / 60;
            extremeMinutes = (extremeLength - swiftness * 24) - (60 * extremeHours);
            legendaryHours = (legendaryLength - swiftness * 72) / 60;
            legendaryMinutes = (legendaryLength - swiftness * 72) - (60 * legendaryHours);

            eb.setTitle("⚔ Dungeon Help Prompt");

            if (message.length == 1) {
                eb.setTitle("⚔ Dungeon Help Menu");

                if (!isPlayerInDungeon(eb)) {
                    dungeonHelp(eb);

                    event.getMessage().replyEmbeds(eb.build())
                            .addActionRow(buttons)
                            .queue();
                } else {
                    event.getMessage().replyEmbeds(eb.build())
                            .queue();
                }

                return;
            }

            if (message[1].equalsIgnoreCase("enter") || message[1].equalsIgnoreCase("start")) {
                // check if player already in dungeon
                if (isPlayerInDungeon(eb)) {
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                if (message.length == 2) {
                    eb.setTitle("⚔ Dungeon Help Prompt");
                    eb.appendDescription("Improper use of command. Please use **dungeon help** for help.");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                if (!diff.equalsIgnoreCase("0")) {
                    eb.appendDescription("*From the previous run you claimed:*\n");
                    claimDungeon(eb, diff);
                }

                if (message[2].equalsIgnoreCase("easy")) {
                    dungeonEnter(eb,"Easy", easyMinutes, 0);
                } else if (message[2].equalsIgnoreCase("normal")) {
                    dungeonEnter(eb, "Normal", normalMinutes, normalHours);
                } else if (message[2].equalsIgnoreCase("challenging")) {
                    dungeonEnter(eb, "Challenging", challengingMinutes, challengingHours);
                } else if (message[2].equalsIgnoreCase("extreme")) {
                    dungeonEnter(eb, "Extreme", extremeMinutes, extremeHours);
                } else if (message[2].equalsIgnoreCase("legendary")) {
                    dungeonEnter(eb, "Legendary", legendaryMinutes, legendaryHours);
                } else {
                    eb.setTitle("⚔ Dungeon Error");
                    eb.appendDescription("Specified difficulty wasn\'t found. Please use **dungeon help** for help.");
                }


            } else if (message[1].equalsIgnoreCase("help")) {
                eb.setTitle("⚔ Dungeon Help Menu");

                dungeonHelp(eb);

                if (!isPlayerInDungeon(eb)) {
                    event.getMessage().replyEmbeds(eb.build())
                            .addActionRow(buttons)
                            .queue();
                } else {
                    event.getMessage().replyEmbeds(eb.build())
                            .queue();
                }

                return;
            } else if (message[1].equalsIgnoreCase("claim")) {
                int notifier = Integer.parseInt(db.getColumn(messageMember.getId(), "dNotifier", "scores"));

                eb.setTitle("⚔ Dungeon Reward Claim");

                if (!inDungeon() && diff.equals("0")) {
                    eb.appendDescription("You have nothing to claim. Enter a dungeon!");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                if (!inDungeon()) {
                    claimDungeon(eb, diff);
                    eb.appendDescription("*Enter another dungeon!*");
                } else {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(db.getColumn(messageMember.getId(), "dTime", "scores"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.appendDescription("Your dungeon run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                }
            } else if (message[1].equalsIgnoreCase("time")) {
                eb.setTitle("⚔ Dungeon Timer");

                isPlayerInDungeon(eb);
            } else {
                eb.setTitle("⚔ Dungeon Error");
                eb.appendDescription("Improper use of the command. Please use **dungeon help** for help.");
            }

            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("easyButton")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            dungeonEnter(eb,"Easy", easyMinutes, 0);

            message.replyEmbeds(eb.build()).queue();
            event.getInteraction().getMessage().delete().queue();
        } else if (event.getButton().getId().equals("normalButton")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            dungeonEnter(eb,"Normal", normalMinutes, normalHours);

            message.replyEmbeds(eb.build()).queue();
            event.getInteraction().getMessage().delete().queue();
        } else if (event.getButton().getId().equals("challengingButton")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            dungeonEnter(eb,"Challenging", challengingMinutes, challengingHours);

            message.replyEmbeds(eb.build()).queue();
            event.getInteraction().getMessage().delete().queue();
        } else if (event.getButton().getId().equals("extremeButton")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            dungeonEnter(eb,"Extreme", extremeMinutes, extremeHours);

            message.replyEmbeds(eb.build()).queue();
            event.getInteraction().getMessage().delete().queue();
        } else if (event.getButton().getId().equals("legendaryButton")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            dungeonEnter(eb,"Legendary", legendaryMinutes, legendaryHours);

            message.replyEmbeds(eb.build()).queue();
            event.getInteraction().getMessage().delete().queue();
        }
    }

    private void dungeonEnter(EmbedBuilder eb, String difficulty, int minutes, int hours) {
        Calendar dungeonTimer = Calendar.getInstance();

        int attackReq = getAttackReq(difficulty);
        int defenceReq = getDefenceReq(difficulty);

        if (profile.getMemberAttack(messageMember) < attackReq) {
            eb.setTitle("⚔ Dungeon Error");
            eb.appendDescription("Your **Attack** is not high enough for this difficulty. Please buy items from the shop.");
            return;
        }

        if (profile.getMemberDefence(messageMember) < defenceReq) {
            eb.setTitle("⚔ Dungeon Error");
            eb.appendDescription("Your **Defence** is not high enough for this difficulty. Please buy items from the shop.");
            return;
        }

        String length = getLengthString(hours, minutes);

        dungeonTimer.add(Calendar.MINUTE, minutes);
        dungeonTimer.add(Calendar.HOUR, hours);
        db.setColumn(messageMember.getId(), "dTime", "" + dungeonTimer.getTimeInMillis(), "scores");
        db.setColumn(messageMember.getId(), "dDiff", difficulty.toLowerCase(), "scores");
        db.setColumn(messageMember.getId(), "dNotifier", "1", "scores");

        eb.setTitle("⚔ Dungeon Entered");
        eb.appendDescription("You've entered the **" + difficulty + "** dungeon! If your run is successful, you will be able" +
                " to claim your rewards after **" + length + "**.");
    }

    private int getAttackReq(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return easyAttackReq;
            case "Normal":
                return normalAttackReq;
            case "Challenging":
                return challengingAttackReq;
            case "Extreme":
                return extremeAttackReq;
            case "Legendary":
                return legendaryAttackReq;
            default:
                return 0;
        }
    }

    private int getDefenceReq(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return easyDefenceReq;
            case "Normal":
                return normalDefenceReq;
            case "Challenging":
                return challengingDefenceReq;
            case "Extreme":
                return extremeDefenceReq;
            case "Legendary":
                return legendaryDefenceReq;
            default:
                return 0;
        }
    }

    public boolean isPlayerInDungeon(EmbedBuilder eb) {
        if (inDungeon()) {
            Calendar now = Calendar.getInstance();
            long dTime = Long.parseLong(db.getColumn(messageMember.getId(), "dTime", "scores"));

            long millisLeft = dTime - now.getTimeInMillis();
            long hoursLeft = millisLeft / (60 * 60 * 1000);
            long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);

            String length = getLengthString((int) hoursLeft, (int) minutesLeft);

            eb.setTitle("⚔ Dungeon Help Prompt");
            eb.appendDescription("You are already in a dungeon. Your run ends in **" + length + "**.");
            return true;
        }

        return false;
    }

    public String getLengthString(int hours, int minutes) {
        String length = "";

        if (hours > 0) length += hours + " hour";
        if (hours > 1) length += "s";

        if (hours > 0 && minutes > 0) length += " and ";

        if (minutes > 0) length +=  minutes + " minute";
        if (minutes > 1) length += "s";

        return length;
    }

    public void claimDungeon(EmbedBuilder eb, String diff) {
        int expToAdd = 0;
        int coinsToAdd = 0;

        int dungeonCounter = Integer.parseInt(db.getColumn(messageMember.getId(), "dCounter", "scores"));

        switch (diff) {
            case "easy":
                expToAdd = (int) (Math.random() * ((easyHigh - easyLow) + 1)) + easyLow;
                coinsToAdd = easyCoins;
                break;
            case "normal":
                expToAdd = (int) (Math.random() * ((normalHigh - normalLow) + 1)) + normalLow;
                coinsToAdd = normalCoins;
                break;
            case "challenging":
                expToAdd = (int) (Math.random() * ((challengingHigh - challengingLow) + 1)) + challengingLow;
                coinsToAdd = challengingCoins;
                break;
            case "extreme":
                expToAdd = (int) (Math.random() * ((extremeHigh - extremeLow) + 1)) + extremeLow;
                coinsToAdd = extremeCoins;
                break;
            case "legendary":
                expToAdd = (int) (Math.random() * ((legendaryHigh - legendaryLow) + 1)) + legendaryLow;
                coinsToAdd = legendaryCoins;
                break;
        }

        int currentExp = Integer.parseInt(db.getColumn(messageMember.getId(), "exp", "scores"));
        int currentCoins = Integer.parseInt(db.getColumn(messageMember.getId(), "coins", "scores"));

        db.setColumn(messageMember.getId(), "exp", "" + (currentExp + expToAdd), "scores");
        db.setColumn(messageMember.getId(), "coins", "" + (currentCoins + coinsToAdd), "scores");
        db.setColumn(messageMember.getId(), "dDiff", "0", "scores");
        db.setColumn(messageMember.getId(), "dNotifier", "0", "scores");

        db.setColumn(messageMember.getId(), "dCounter", "" + (dungeonCounter + 1), "scores");

        eb.appendDescription("+ \uD83C\uDF1F **" + expToAdd + "** experience \n+ \uD83D\uDCC0 **" + coinsToAdd + "** coins\n\n");

        if (levelUpNotifier.checkForLevelUp(messageMember)) {
            eb.appendDescription("*After claiming your reward, you leveled up!*\n");
        }
    }

    public void dungeonHelp(EmbedBuilder eb) {
        eb.setTitle("⚔ Dungeon Help Menu");

        String help = "Use **dungeon enter** *[difficulty]* to enter a dungeon!\n\n*Difficulties:*\n";
        eb.appendDescription(help);

        eb.addField("\uD83D\uDFE9 **Easy**\n", "*Attack Requirement* : " + easyAttackReq + "\n" +
                "↳ *Defence Requirement* : " + easyDefenceReq + "\n" +
                "↳ *Reward* : " + easyLow + " - " + easyHigh + " exp, " + easyCoins + " coins\n" +
                "↳ *Time Length* : " + easyLength,
                true);
        eb.addField("\uD83D\uDFE7 **Normal**\n",
                "↳ *Attack Requirement* : " + normalAttackReq + "\n" +
                "↳ *Defence Requirement* : " + normalDefenceReq + "\n" +
                "↳ *Reward* : " + normalLow + " - " + normalHigh + " exp, " + normalCoins + " coins\n" +
                "↳ *Time Length* : " + normalLength + "\n",
                true);
        eb.addField("", "――――――――――――――――", false);
        eb.addField("\uD83D\uDFE5 **Challenging**\n",
                "↳ *Attack Requirement* : " + challengingAttackReq + "\n" +
                "↳ *Defence Requirement* : " + challengingDefenceReq + "\n" +
                "↳ *Reward* : " + challengingLow + " - " + challengingHigh + " exp, " + challengingCoins + " coins\n" +
                "↳ *Time Length* : " + challengingLength + "\n",
                true);
        eb.addField("☢ **Extreme**\n",
                "↳ *Attack Requirement* : " + extremeAttackReq + "\n" +
                "↳ *Defence Requirement* : " + extremeDefenceReq + "\n" +
                "↳ *Reward* : " + extremeLow + " - " + extremeHigh + " exp, " + extremeCoins + " coins\n" +
                "↳ *Time Length* : " + extremeLength + "\n",
                true);
        eb.addField("", "――――――――――――――――", false);
        eb.addField("\uD83C\uDFF5 **Legendary**\n",
                "↳ *Attack Requirement* : " + legendaryAttackReq + "\n" +
                "↳ *Defence Requirement* : " + legendaryDefenceReq + "\n" +
                "↳ *Reward* : " + legendaryLow + " - " + legendaryHigh + " exp, " + legendaryCoins + " coins\n" +
                "↳ *Time Length* : " + legendaryLength, true);
    }

    public boolean inDungeon() {
        Calendar now = Calendar.getInstance();
        long dTime = Long.parseLong(db.getColumn(messageMember.getId(), "dTime", "scores"));

        if (now.getTimeInMillis() <= dTime) {
            return true;
        } else {
            return false;
        }

    }

    public boolean inDungeon(Member member) {
        Calendar now = Calendar.getInstance();
        long dTime = Long.parseLong(db.getColumn(member.getId(), "dTime", "scores"));

        if (now.getTimeInMillis() <= dTime) {
            return true;
        } else {
            return false;
        }
    }
}
