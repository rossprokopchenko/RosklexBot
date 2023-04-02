package com.rosklex.listeners.commands;

import com.rosklex.listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Store extends ListenerAdapter {
    public static final int pickaxePrice = 100;
    public static final int pickaxeDurability = 10;
    public static final int stickPrice = 100;
    public static final int stickAttack = 1;
    public static final int hammerPrice = 300;
    public static final int hammerAttack = 4;
    public static final int daggerPrice = 750;
    public static final int daggerAttack = 10;
    public static final int gunPrice = 2000;
    public static final int gunAttack = 30;

    public static final int orbPrice = 1000;
    public static final int orbDefence = 10;
    public static final int shieldPrice = 2000;
    public static final int shieldDefence = 25;

    public static final int bootsPrice = 1500;
    public static final int bootsSwiftness = 1;

    public static final int mKnifeAttack = 30;
    public static final int mShieldDefence = 30;
    public static final int hourglassSwiftness = 3;

    public static final int junkPrice = 2;
    public static final int bagsPrice = 30;
    public static final int diamondsPrice = 300;

    private final String ownedItem = ", you already own this item. YIKES";
    private final String noCoins = "You don\'t have enough coins, silly";
    private final String purchasedItem1 = ", congratulations on purchasing a ";
    private final String purchasedItem2 = "** coins has been deducted from your account.";

    private DatabaseManager db;
    private Profile profile;
    private Inventory inventory;

    public Store(DatabaseManager db, Profile profile, Inventory inventory) {
        this.db = db;
        this.profile = profile;
        this.inventory = inventory;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("store") || message[0].equalsIgnoreCase("shop")) {
            int balance = Integer.parseInt(db.getColumn(member.getId(), "coins", "scores"));
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.GREEN);
            eb.setTimestamp(date.toInstant());

            eb.setTitle("\uD83D\uDECD Item Store");
            eb.setDescription("Your balance is: **" + balance + "** coins");
            eb.addField("Item (Price)",
                    "⛏ Standard **Pickaxe** (" + (pickaxePrice + (profile.getMemberLevel(member) * 15)) + ") : " + getDefinition("Pickaxe") + "\n" +
                            "\n[ *Combat Tools* ]\n\n" +
                            "\uD83E\uDDAF Walking **Stick** (" + stickPrice + ") : " + getDefinition("Walking Stick") + "\n" +
                            "\uD83D\uDD28 A Handyman's **Hammer** (" + hammerPrice + ") : " + getDefinition("Hammer") + "\n" +
                            "\uD83D\uDDE1 **Dagger** (" + daggerPrice + ") : " + getDefinition("Dagger") + "\n" +
                            "\uD83D\uDD2B Paintball **Gun** (" + gunPrice + ") : " + getDefinition("Paintball Gun") + "\n" +

                            "\n[ *Defence Pieces* ]\n\n" +
                            "\uD83D\uDD2E Protection **Orb** (" + orbPrice + ") : " + getDefinition("Protection Orb") + "\n" +
                            "\uD83D\uDEE1 Fighter's **Shield** (" + shieldPrice + ") : " + getDefinition("Shield") + "\n" +

                            "\n[ *Accessories* ]\n\n" +
                            "\uD83D\uDC5F Mobility **Boots** (" + bootsPrice + ") : " + getDefinition("Boots") + "\n" +
                            "", true);

            eb.addField("", "Use **buy** *[item in bold]* to purchase the items listed above.", false);
            eb.setFooter("Item Store", member.getUser().getAvatarUrl());
            eb.setTimestamp(date.toInstant());

            event.getMessage().replyEmbeds(eb.build()).queue();
        } else if (message[0].equalsIgnoreCase("buy")) {

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.CYAN);
            eb.setTimestamp(date.toInstant());

            if (message.length == 1) {
                eb.setTitle("\uD83D\uDECD Improper use of the command");
                eb.setDescription("See **store** to see all the items available for purchase.");
                event.getMessage().replyEmbeds(eb.build()).queue();
                return;
            }

            int balance = Integer.parseInt(db.getColumn(member.getId(), "coins", "scores"));
            int currentAttack = profile.getMemberAttack(member);
            int currentDefence = profile.getMemberDefence(member);
            int currentSwiftness = profile.getMemberSwiftness(member);

            if (message[1].equalsIgnoreCase("pickaxe") || message[1].equalsIgnoreCase("p")) {

                if (balance < (pickaxePrice + (profile.getMemberLevel(member) * 15))) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "⛏ **Standard Pickaxe**" +
                        "\n**" + (pickaxePrice + (profile.getMemberLevel(member) * 15)) + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - (pickaxePrice + (profile.getMemberLevel(member) * 15))) + " coins.", false);

                db.setColumn(member.getId(), "pickaxe", "" + (inventory.getPickDurability(member) + pickaxeDurability), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - (pickaxePrice + (profile.getMemberLevel(member) * 15))), "scores");

            } else if (message[1].equalsIgnoreCase("stick")) {

                if (balance < stickPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83E\uDDAF **Walking Stick**" +
                        "\n**" + stickPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - stickPrice) + " coins.", false);

                db.setColumn(member.getId(), "stick", "" + (inventory.getStick(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - stickPrice), "scores");

            } else if (message[1].equalsIgnoreCase("hammer")) {

                if (balance < hammerPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDD28 **Handyman's Hammer**" +
                        "\n**" + hammerPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - hammerPrice) + " coins.", false);

                db.setColumn(member.getId(), "hammer", "" + (inventory.getHammer(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - hammerPrice), "scores");

            } else if (message[1].equalsIgnoreCase("dagger")) {

                if (balance < daggerPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDDE1 **Dagger**" +
                        "\n**" + daggerPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - daggerPrice) + " coins.", false);

                db.setColumn(member.getId(), "dagger", "" + (inventory.getDagger(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - daggerPrice), "scores");

            } else if (message[1].equalsIgnoreCase("gun")) {

                if (balance < gunPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDD2B **Paintball Gun**" +
                        "\n**" + gunPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - gunPrice) + " coins.", false);

                db.setColumn(member.getId(), "gun", "" + (inventory.getGun(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - gunPrice), "scores");

            } else if (message[1].equalsIgnoreCase("orb")) {

                if (balance < orbPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDD2E ** Protection Orb**" +
                        "\n**" + orbPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - orbPrice) + " coins.", false);

                db.setColumn(member.getId(), "orb", "" + (inventory.getProtectionOrb(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - orbPrice), "scores");

            } else if (message[1].equalsIgnoreCase("shield")) {

                if (balance < shieldPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDEE1 **Fighter's Shield**" +
                        "\n**" + shieldPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - shieldPrice) + " coins.", false);

                db.setColumn(member.getId(), "shield", "" + (inventory.getShield(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - shieldPrice), "scores");

            } else if (message[1].equalsIgnoreCase("boots")) {

                if (balance < bootsPrice) {
                    eb.setTitle("\uD83D\uDECD Insufficient Funds");
                    eb.setDescription(noCoins);
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                if (currentSwiftness == 10) {
                    eb.setTitle("\uD83D\uDECD Item Error");
                    eb.setDescription("You've reached max swiftness.");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                eb.setTitle("\uD83D\uDECD Item Purchased");
                eb.setDescription(member.getUser().getName() + purchasedItem1 + "\uD83D\uDC5F **Mobility Boots**" +
                        "\n**" + bootsPrice + purchasedItem2);
                eb.addField("", "Your new balance is " + (balance - bootsPrice) + " coins.", false);

                db.setColumn(member.getId(), "boots", "" + (inventory.getBoots(member) + 1), "scores");
                db.setColumn(member.getId(), "coins", "" + (balance - bootsPrice), "scores");

            } else {
                eb.setTitle("\uD83D\uDECD Item Not Found");
                eb.setDescription("This item is not in the store. See **store** to see all the items available for purchase.");
            }

            event.getMessage().replyEmbeds(eb.build()).queue();

        } else if (message[0].equalsIgnoreCase("sell")) {
            int balance = Integer.parseInt(db.getColumn(member.getId(), "coins", "scores"));

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.CYAN);
            eb.setTimestamp(date.toInstant());

            if (message.length == 1) {
                eb.setTitle("\uD83D\uDECD Improper use of the command");
                eb.setDescription("Check your inventory for all the items that you can sell.");
                event.getMessage().replyEmbeds(eb.build()).queue();
                return;
            }

            if (message[1].equalsIgnoreCase("junk")) {
                int numJunk = inventory.getJunk(member);

                if (numJunk == 0) {
                    eb.setTitle("\uD83D\uDECD Item Error");
                    eb.setDescription("You do not own any junk!");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;

                }

                int junkSell = (int) (profile.getMemberLevel(member) * 0.5) * numJunk + junkPrice;

                eb.setTitle("\uD83D\uDECD Item Sold");
                eb.setDescription(member.getUser().getName() + ", You sold **" + numJunk + "** junk for **" + junkSell + "** coins.");
                eb.addField("", "Your new balance is " + (balance + junkSell) + " coins.", false);

                db.setColumn(member.getId(), "coins", "" + (balance + junkSell), "scores");
                db.setColumn(member.getId(), "gear", "0", "scores");
                db.setColumn(member.getId(), "wrench", "0", "scores");
                db.setColumn(member.getId(), "rustyKey", "0", "scores");
                db.setColumn(member.getId(), "battery", "0", "scores");

            } else if (message[1].equalsIgnoreCase("bag") || message[1].equalsIgnoreCase("bags")) {
                int bags = inventory.getMoneyBags(member);

                if (bags == 0) {
                    eb.setTitle("\uD83D\uDECD Item Error");
                    eb.setDescription("You do not own any bags of coins!");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;

                }

                int bagSell = (profile.getMemberLevel(member) * 5) * bags + bagsPrice;

                eb.setTitle("\uD83D\uDECD Item Sold");
                eb.setDescription(member.getUser().getName() + ", You sold **" + bags + "** bag(s) of coins for **" + bagSell + "** coins!");
                eb.addField("", "Your new balance is " + (balance + bagSell) + " coins.", false);

                db.setColumn(member.getId(), "coins", "" + (balance + bagSell), "scores");
                db.setColumn(member.getId(), "moneyBag", "0", "scores");
            } else if (message[1].equalsIgnoreCase("diamond") || message[1].equalsIgnoreCase("diamonds")) {
                int diamonds = inventory.getDiamonds(member);

                if (diamonds == 0) {
                    eb.setTitle("\uD83D\uDECD Item Error");
                    eb.setDescription("You do not own any diamonds!");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;

                }

                int diamondSell = (profile.getMemberLevel(member) * 10) * diamonds + diamondsPrice;

                eb.setTitle("\uD83D\uDECD Item Sold");
                eb.setDescription(member.getUser().getName() + ", You sold **" + diamonds + "** diamond(s) for **" + diamondSell + "** coins!");
                eb.addField("", "Your new balance is " + (balance + diamondSell) + " coins.", false);

                db.setColumn(member.getId(), "coins", "" + (balance + (diamondSell)), "scores");
                db.setColumn(member.getId(), "diamond", "0", "scores");
            } else if (message[1].equalsIgnoreCase("mine") && message.length == 3 && message[2].equalsIgnoreCase("all")) {
                int numJunk = inventory.getJunk(member);
                int bags = inventory.getMoneyBags(member);
                int diamonds = inventory.getDiamonds(member);

                if (numJunk == 0 && bags == 0 && diamonds == 0) {
                    eb.setTitle("\uD83D\uDECD Item Error");
                    eb.setDescription("You do not have any mine loot.");
                    event.getMessage().replyEmbeds(eb.build()).queue();
                    return;
                }

                int totalSell = 0;

                if (diamonds > 0) {
                    totalSell += (profile.getMemberLevel(member) * 5) * diamonds + diamondsPrice;
                }
                if (bags > 0) {
                    totalSell += (profile.getMemberLevel(member) * 10) * bags + bagsPrice;
                }
                if (numJunk > 0) {
                    totalSell += (int) (profile.getMemberLevel(member) * 0.5) * numJunk + junkPrice;
                }

                eb.setTitle("\uD83D\uDECD Item Sold");
                eb.setDescription(member.getUser().getName() + ", You sold all of your mine loot for **" + totalSell + "** coins!");
                eb.addField("", "Your new balance is " + (balance + totalSell) + " coins.", false);

                db.setColumn(member.getId(), "coins", "" + (balance + totalSell), "scores");
                db.setColumn(member.getId(), "gear", "0", "scores");
                db.setColumn(member.getId(), "wrench", "0", "scores");
                db.setColumn(member.getId(), "rustyKey", "0", "scores");
                db.setColumn(member.getId(), "battery", "0", "scores");
                db.setColumn(member.getId(), "moneyBag", "0", "scores");
                db.setColumn(member.getId(), "diamond", "0", "scores");
            } else {
                eb.setTitle("\uD83D\uDECD Item Not Found");
                eb.setDescription("This item is not in your mine loot. See **inv** to see the mine loot that you have.");
            }

            event.getMessage().replyEmbeds(eb.build()).queue();
        }
    }

    public String getDefinition(String item) {
        Map<String, String> items = new HashMap<>();

        items.put("Pickaxe", "Pretty self-explanatory isn\'t it? This gives you the ability to mine. You can make more money!");
        items.put("Walking Stick", "+" + stickAttack + " Attack, *Cheap, but can be pretty useful.*");
        items.put("Hammer", "+" + hammerAttack + " Attack, *Throw it maybe? It doesn\'t hurt I promise.*");
        items.put("Dagger", "+" + daggerAttack + " Attack, *Perfect for close combat.*");
        items.put("Paintball Gun", "+" + gunAttack + " Attack, *It's a regular  paintball gun.*");
        items.put("Protection Orb", "+" + orbDefence + " Defence, *Orbits your character and grants protection.*");
        items.put("Shield", "+" + shieldDefence + " Defence, *Help's you in close combat.*");
        items.put("Boots", "+" + bootsSwiftness + " Swiftness, *Let's you complete dungeon runs faster.*");

        return items.get(item);
    }
}
