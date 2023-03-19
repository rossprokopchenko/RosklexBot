package commands;

import events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Dungeon extends ListenerAdapter {

    private final int easyLow = 25;
    private final int easyHigh = 50;
    private final int easyCoins = 50;
    private final int easyAttackReq = 0;
    private final int easyDefenceReq = 0;

    private final int normalLow = 100;
    private final int normalHigh = 200;
    private final int normalCoins = 200;
    private final int normalAttackReq = 5;
    private final int normalDefenceReq = 0;

    private final int challengingLow = 500;
    private final int challengingHigh = 1000;
    private final int challengingCoins = 500;
    private final int challengingAttackReq = 15;
    private final int challengingDefenceReq = 10;

    private final int extremeLow = 1000;
    private final int extremeHigh = 2000;
    private final int extremeCoins = 1000;
    private final int extremeAttackReq = 50;
    private final int extremeDefenceReq = 25;

    private final int legendaryLow = 5000;
    private final int legendaryHigh = 7500;
    private final int legendaryCoins = 3000;
    private final int legendaryAttackReq = 200;
    private final int legendaryDefenceReq = 100;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("dungeon")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.YELLOW);
            eb.setTimestamp(date.toInstant());

            eb.setTitle("⚔ Dungeon Help Prompt");

            if (message.length == 1) {
                eb.setTitle("⚔ Dungeon Help Menu");

                // check if player already in dungeon
                if (inDungeon(member)) {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.setDescription("Your dungeon run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                } else {
                    eb.addField("Difficulties, use **dungeon enter** *[difficulty]* to enter a dungeon",
                            dungeonHelp(member), true);
                }

                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            }

            if (message[1].equalsIgnoreCase("enter") || message[1].equalsIgnoreCase("start")) {
                Calendar dungeonTimer = Calendar.getInstance();

                // check if player already in dungeon
                if (inDungeon(member)) {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.setTitle("⚔ Dungeon Help Prompt");
                    eb.setDescription("You are already in a dungeon. Your run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                    event.getChannel().sendMessageEmbeds(eb.build()).queue();
                    return;
                }

                if (message.length == 2) {
                    eb.setTitle("⚔ Dungeon Help Prompt");
                    eb.setDescription("Improper use of command. Please use **dungeon help** for help.");
                    event.getChannel().sendMessageEmbeds(eb.build()).queue();
                    return;
                }


                int swiftness = Profile.getMemberSwiftness(member);

                int easyMinutes = (30 - swiftness);

                int normalHours = (120 - swiftness * 4) / 60;
                int normalMinutes = (120 - swiftness * 4) - (60 * normalHours);

                int challengingHours = (360 - swiftness * 12) / 60;
                int challengingMinutes = (360 - swiftness * 12) - (60 * challengingHours);

                int extremeHours = (720 - swiftness * 24) / 60;
                int extremeMinutes = (720 - swiftness * 24) - (60 * extremeHours);

                int legendaryHours = (2160 - swiftness * 72) / 60;
                int legendaryMinutes = (2160 - swiftness * 72) - (60 * legendaryHours);

                String normalLength = "";
                String challengingLength = "";
                String extremeLength = "";
                String legendaryLength = "";

                if (normalHours > 1) {
                    normalLength += normalHours + " hours ";
                } else if (normalHours == 1) {
                    normalLength += normalHours + " hour ";
                }

                if (normalMinutes > 1) {
                    normalLength += normalMinutes + " minutes";
                } else if (normalMinutes == 1) {
                    normalLength += normalMinutes + " minute";
                }

                if (challengingHours > 1) {
                    challengingLength += challengingHours + " hours ";
                } else if (challengingHours == 1) {
                    challengingLength += challengingHours + " hour ";
                }

                if (challengingMinutes > 1) {
                    challengingLength += challengingMinutes + " minutes";
                } else if (normalMinutes == 1) {
                    challengingLength += challengingMinutes + " minute";
                }

                if (extremeHours > 1) {
                    extremeLength += extremeHours + " hours ";
                } else if (normalMinutes == 1) {
                    extremeLength += extremeHours + " hour ";
                }

                if (extremeMinutes > 1) {
                    extremeLength += extremeMinutes + " minutes";
                } else if (normalMinutes == 1) {
                    extremeLength += extremeMinutes + " minute";
                }

                if (legendaryHours > 1) {
                    legendaryLength += legendaryHours + " hours ";
                } else if (legendaryHours == 1) {
                    legendaryLength += legendaryHours + " hour ";
                }

                if (legendaryMinutes > 1) {
                    legendaryLength += legendaryMinutes + " minutes";
                } else if (normalMinutes == 1) {
                    legendaryLength += legendaryMinutes + " minute";
                }


                if (message[2].equalsIgnoreCase("easy")) {

                    dungeonTimer.add(Calendar.MINUTE, easyMinutes);
                    Database.getDb().setColumn(member.getId(), "dTime", "" + dungeonTimer.getTimeInMillis());
                    Database.getDb().setColumn(member.getId(), "dDiff", "easy");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "1");

                    eb.setTitle("⚔ Dungeon Entered");
                    eb.setDescription("You've entered the **Easy** dungeon! If your run is successful, you will be able" +
                            " to claim your rewards after **" + easyMinutes + " minutes**.");

                } else if (message[2].equalsIgnoreCase("normal")) {

                    if (Profile.getMemberAttack(member) < normalAttackReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Attack** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    if (Profile.getMemberDefence(member) < normalDefenceReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Defence** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    dungeonTimer.add(Calendar.MINUTE, normalMinutes);
                    dungeonTimer.add(Calendar.HOUR, normalHours);
                    Database.getDb().setColumn(member.getId(), "dTime", "" + dungeonTimer.getTimeInMillis());
                    Database.getDb().setColumn(member.getId(), "dDiff", "normal");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "1");

                    eb.setTitle("⚔ Dungeon Entered");
                    eb.setDescription("You've entered the **Normal** dungeon! If your run is successful, you will be able" +
                            " to claim your rewards after **" + normalLength + "**.");

                } else if (message[2].equalsIgnoreCase("challenging")) {

                    if (Profile.getMemberAttack(member) < challengingAttackReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Attack** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    if (Profile.getMemberDefence(member) < challengingDefenceReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Defence** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    dungeonTimer.add(Calendar.MINUTE, challengingMinutes);
                    dungeonTimer.add(Calendar.HOUR, challengingHours);
                    Database.getDb().setColumn(member.getId(), "dTime", "" + dungeonTimer.getTimeInMillis());
                    Database.getDb().setColumn(member.getId(), "dDiff", "challenging");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "1");

                    eb.setTitle("⚔ Dungeon Entered");
                    eb.setDescription("You've entered the **Challenging** dungeon! If your run is successful, you will be able" +
                            " to claim your rewards after **" + challengingLength + "**.");

                } else if (message[2].equalsIgnoreCase("extreme")) {

                    if (Profile.getMemberAttack(member) < extremeAttackReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Attack** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    if (Profile.getMemberDefence(member) < extremeDefenceReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Defence** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    dungeonTimer.add(Calendar.MINUTE, extremeMinutes);
                    dungeonTimer.add(Calendar.HOUR, extremeHours);
                    Database.getDb().setColumn(member.getId(), "dTime", "" + dungeonTimer.getTimeInMillis());
                    Database.getDb().setColumn(member.getId(), "dDiff", "extreme");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "1");

                    eb.setTitle("⚔ Dungeon Entered");
                    eb.setDescription("You've entered the **Extreme** dungeon! If your run is successful, you will be able" +
                            " to claim your rewards after **" + extremeLength + "**.");

                } else if (message[2].equalsIgnoreCase("legendary")) {

                    if (Profile.getMemberAttack(member) < legendaryAttackReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Attack** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    if (Profile.getMemberDefence(member) < legendaryDefenceReq) {
                        eb.setTitle("⚔ Dungeon Error");
                        eb.setDescription("Your **Defence** is not high enough for this difficulty. Please buy items from the shop.");
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }

                    dungeonTimer.add(Calendar.MINUTE, legendaryMinutes);
                    dungeonTimer.add(Calendar.HOUR, legendaryHours);
                    Database.getDb().setColumn(member.getId(), "dTime", "" + dungeonTimer.getTimeInMillis());
                    Database.getDb().setColumn(member.getId(), "dDiff", "legendary");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "1");

                    eb.setTitle("⚔ Dungeon Entered");
                    eb.setDescription("You've entered the **Legendary** dungeon! If your run is successful, you will be able" +
                            " to claim your rewards after **" + legendaryLength + "**.");

                } else {
                    eb.setTitle("⚔ Dungeon Error");
                    eb.setDescription("Specified difficulty wasn\'t found. Please use **dungeon help** for help.");
                }


            } else if (message[1].equalsIgnoreCase("help")) {
                eb.setTitle("⚔ Dungeon Help Menu");

                // check if player already in dungeon
                if (inDungeon(member)) {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.setTitle("⚔ Dungeon Help Prompt");
                    eb.setDescription("Your dungeon run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                }

                eb.addField("Difficulties, use **dungeon enter** *[difficulty]* to enter a dungeon",
                        dungeonHelp(member), true);

                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            } else if (message[1].equalsIgnoreCase("claim")) {
                int notifier = Integer.parseInt(Database.getDb().getColumn(member.getId(), "dNotifier"));
                String diff = Database.getDb().getColumn(member.getId(), "dDiff");

                eb.setTitle("⚔ Dungeon Reward Claim");

                if (!inDungeon(member) && diff.equals("0")) {
                    eb.setDescription("You have nothing to claim. Enter another dungeon!");
                    event.getChannel().sendMessageEmbeds(eb.build()).queue();
                    return;
                }

                if (!inDungeon(member)) {
                    int expToAdd = 0;
                    int coinsToAdd = 0;

                    int dungeonCounter = Integer.parseInt(Database.getDb().getColumn(member.getId(), "dCounter"));

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

                    int currentExp = Integer.parseInt(Database.getDb().getColumn(member.getId(), "exp"));
                    int currentCoins = Integer.parseInt(Database.getDb().getColumn(member.getId(), "coins"));

                    Database.getDb().setColumn(member.getId(), "exp", "" + (currentExp + expToAdd));
                    Database.getDb().setColumn(member.getId(), "coins", "" + (currentCoins + coinsToAdd));
                    Database.getDb().setColumn(member.getId(), "dDiff", "0");
                    Database.getDb().setColumn(member.getId(), "dNotifier", "0");

                    Database.getDb().setColumn(member.getId(), "dCounter", "" + (dungeonCounter + 1));

                    eb.setDescription("From your previous run, you claimed " + expToAdd + " exp, " + coinsToAdd + " coins. Enter another one!");

                } else {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.setDescription("Your dungeon run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                }
            } else if (message[1].equalsIgnoreCase("time")) {
                eb.setTitle("⚔ Dungeon Timer");

                if (inDungeon(member)) {
                    Calendar now = Calendar.getInstance();
                    long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

                    long millisLeft = dTime - now.getTimeInMillis();
                    long hoursLeft = millisLeft / (60 * 60 * 1000);
                    long minutesLeft = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsLeft = (millisLeft / 1000) % 60;

                    String hours = Long.toString(hoursLeft);
                    String minutes = Long.toString(minutesLeft);
                    String seconds = Long.toString(secondsLeft);

                    eb.setDescription("Your dungeon run ends in " + hours + " hours, " +
                            minutes + " minutes and " + seconds + " seconds.");
                } else {
                    eb.setDescription("You are currently not in a dungeon. Use **dungeon help** to get help on dungeons.");
                }
            } else {
                eb.setTitle("⚔ Dungeon Error");
                eb.setDescription("Improper use of the command. Please use **dungeon help** for help.");
            }

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public String dungeonHelp(Member member) {
        int swiftness = Profile.getMemberSwiftness(member);

        int easyMinutes = (30 - swiftness);
        String easyLength = easyMinutes + " minutes";

        int normalHours = (120 - swiftness * 4) / 60;
        int normalMinutes = (120 - swiftness * 4) - (60 * normalHours);

        int challengingHours = (360 - swiftness * 12) / 60;
        int challengingMinutes = (360 - swiftness * 12) - (60 * challengingHours);

        int extremeHours = (720 - swiftness * 24) / 60;
        int extremeMinutes = (720 - swiftness * 24) - (60 * extremeHours);

        int legendaryHours = (2160 - swiftness * 72) / 60;
        int legendaryMinutes = (2160 - swiftness * 72) - (60 * legendaryHours);

        String normalLength = "";
        String challengingLength = "";
        String extremeLength = "";
        String legendaryLength = "";

        if (normalHours > 1) {
            normalLength += normalHours + " hours ";
        } else if (normalHours == 1) {
            normalLength += normalHours + " hour ";
        }

        if (normalMinutes > 1) {
            normalLength += normalMinutes + " minutes";
        } else if (normalMinutes == 1) {
            normalLength += normalMinutes + " minute";
        }

        if (challengingHours > 1) {
            challengingLength += challengingHours + " hours ";
        } else if (challengingHours == 1) {
            challengingLength += challengingHours + " hour ";
        }

        if (challengingMinutes > 1) {
            challengingLength += challengingMinutes + " minutes";
        } else if (normalMinutes == 1) {
            challengingLength += challengingMinutes + " minute";
        }

        if (extremeHours > 1) {
            extremeLength += extremeHours + " hours ";
        } else if (normalMinutes == 1) {
            extremeLength += extremeHours + " hour ";
        }

        if (extremeMinutes > 1) {
            extremeLength += extremeMinutes + " minutes";
        } else if (normalMinutes == 1) {
            extremeLength += extremeMinutes + " minute";
        }

        if (legendaryHours > 1) {
            legendaryLength += legendaryHours + " hours ";
        } else if (legendaryHours == 1) {
            legendaryLength += legendaryHours + " hour ";
        }

        if (legendaryMinutes > 1) {
            legendaryLength += legendaryMinutes + " minutes";
        } else if (normalMinutes == 1) {
            legendaryLength += legendaryMinutes + " minute";
        }

        String help = "\uD83D\uDFE9 **Easy** : pretty much no challenge, \"low risk low reward\"\n" +
                "↳ *Attack Requirement* : " + easyAttackReq + "\n" +
                "↳ *Defence Requirement* : " + easyDefenceReq + "\n" +
                "↳ *Reward* : " + easyLow + " - " + easyHigh + " exp, " + easyCoins + " coins\n" +
                "↳ *Time Length* : " + easyLength + "\n" +
                "\uD83D\uDFE7 **Normal** : a casual run with decent rewards\n" +
                "↳ *Attack Requirement* : " + normalAttackReq + "\n" +
                "↳ *Defence Requirement* : " + normalDefenceReq + "\n" +
                "↳ *Reward* : " + normalLow + " - " + normalHigh + " exp, " + normalCoins + " coins\n" +
                "↳ *Time Length* : " + normalLength + "\n" +
                "\uD83D\uDFE5 **Challenging** : an all guns blazing challenge (unless you\'re OP)\n" +
                "↳ *Attack Requirement* : " + challengingAttackReq + "\n" +
                "↳ *Defence Requirement* : " + challengingDefenceReq + "\n" +
                "↳ *Reward* : " + challengingLow + " - " + challengingHigh + " exp, " + challengingCoins + " coins\n" +
                "↳ *Time Length* : " + challengingLength + "\n" +
                "☢ **Extreme** : you're playing with fire and bad odds\n" +
                "↳ *Attack Requirement* : " + extremeAttackReq + "\n" +
                "↳ *Defence Requirement* : " + extremeDefenceReq + "\n" +
                "↳ *Reward* : " + extremeLow + " - " + extremeHigh + " exp, " + extremeCoins + " coins\n" +
                "↳ *Time Length* : " + extremeLength + "\n" +
                "\uD83C\uDFF5 **Legendary** : only the bravest would enter this mode\n" +
                "↳ *Attack Requirement* : " + legendaryAttackReq + "\n" +
                "↳ *Defence Requirement* : " + legendaryDefenceReq + "\n" +
                "↳ *Reward* : " + legendaryLow + " - " + legendaryHigh + " exp, " + legendaryCoins + " coins\n" +
                "↳ *Time Length* : " + legendaryLength + "\n";

        return help;
    }

    public static boolean inDungeon(Member member) {
        Calendar now = Calendar.getInstance();
        long dTime = Long.parseLong(Database.getDb().getColumn(member.getId(), "dTime"));

        if (now.getTimeInMillis() <= dTime) {
            return true;
        } else {
            return false;
        }

    }
}
