package listeners.commands;

import listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import database.DatabaseManager;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Mine extends ListenerAdapter {
    private DatabaseManager db;
    private Profile profile;
    private Inventory inventory;

    public Mine(DatabaseManager db, Profile profile, Inventory inventory) {
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

        message[0] = message[0].substring(1);

        if (message[0].equalsIgnoreCase("mine")) {
            int pickaxeDurability = Integer.parseInt(db.getColumn(member.getId(), "pickaxe"));

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.CYAN);
            eb.setTimestamp(date.toInstant());

            if (pickaxeDurability == 0) {
                eb.setAuthor("⛏ Mine Error");
                eb.setDescription("You do not own a pickaxe. Please buy one from **shop**");
                eb.setFooter("Mine Error", member.getUser().getAvatarUrl());
                event.getChannel().sendMessageEmbeds(eb.build()).queue();
                return;
            }

            eb.setAuthor("⛏ Mine Results for " + member.getUser().getName());
            eb.setFooter("Mine Results", member.getUser().getAvatarUrl());

            double rand = round((Math.random() * 100), 2);

            if (rand <= 85) {
                handleCommon(eb, member);
            } else if (rand > 85 && rand <= 97.5) {
                handleRare(eb, member);
            } else if (rand > 97.5 && rand <= 99.7) {
                handleExtraOrd(eb, member);
            } else if (rand > 99.7 && rand <= 99.95) {
                handleMythical(eb, member);
            } else if (rand > 99.95 && rand <= 100) {
                handleLegendary(event, eb, member);
            }

            db.setColumn(member.getId(), "pickaxe", "" + (pickaxeDurability - 1));

            if ((pickaxeDurability - 1) == 0) {
                eb.addField("", "**At the last moment, your pickaxe shattered. Buy another one from the store.**", false);
            }

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    private void handleCommon(EmbedBuilder eb, Member member) {
        eb.setDescription("After some time in the mine, unfortunately you came back with some junk.");

        int rand2 = (int) (Math.random() * 4);

        if (rand2 == 0) {

            eb.addField("**Results**", "You found a gear ⚙ Ain'\t that dandy?", false);
            int gearAmount = Integer.parseInt(db.getColumn(member.getId(), "gear"));
            db.setColumn(member.getId(), "gear", "" + (gearAmount + 1));

        } else if (rand2 == 1) {

            eb.addField("**Results**", "You found a wrench \uD83D\uDD27 Maybe try again?", false);
            int wrenchAmount = Integer.parseInt(db.getColumn(member.getId(), "wrench"));
            db.setColumn(member.getId(), "wrench", "" + (wrenchAmount + 1));

        } else if (rand2 == 2) {

            eb.addField("**Results**", "You found a rusty key \uD83D\uDD11 Doubt it'd be useful for anything...", false);
            int rustyKeyAmount = Integer.parseInt(db.getColumn(member.getId(), "rustyKey"));
            db.setColumn(member.getId(), "rustyKey", "" + (rustyKeyAmount + 1));

        } else if (rand2 == 3) {

            eb.addField("**Results**", "You found a battery \uD83D\uDD0B Must've been from some previous miner.", false);
            int batteryAmount = Integer.parseInt(db.getColumn(member.getId(), "battery"));
            db.setColumn(member.getId(), "battery", "" + (batteryAmount + 1));

        }
    }

    private void handleRare(EmbedBuilder eb, Member member) {
        eb.setColor(Color.YELLOW);
        eb.setDescription("You came across a rare item!");

        int rand2 = (int) (Math.random() * 2);

        int expToGain = 5 + profile.getMemberLevel(member);
        db.setColumn(member.getId(), "exp", "" + (profile.getMemberExp(member) + expToGain));

        if (rand2 == 0) {

            eb.addField("**Results**", "It's a small bag of coins \uD83D\uDCB0 Cash that in, it could be worth a fortune.\n" +
                    "You also gained " + (expToGain) + " experience.", false);
            int moneyBagAmount = Integer.parseInt(db.getColumn(member.getId(), "moneyBag"));
            db.setColumn(member.getId(), "moneyBag", "" + (moneyBagAmount + 1));

        } else if (rand2 == 1) {

            eb.addField("**Results**", "It's a small bag of coins \uD83D\uDCB0 Who left that laying around?\n" +
                    "You also gained " + (expToGain) + " experience.", false);
            int moneyBagAmount = Integer.parseInt(db.getColumn(member.getId(), "moneyBag"));
            db.setColumn(member.getId(), "moneyBag", "" + (moneyBagAmount + 1));
        }
    }

    private void handleExtraOrd(EmbedBuilder eb, Member member) {
        eb.setColor(Color.GREEN);
        eb.setDescription("You dug up an extra ordinary item!");

        int expToGain = 30 + profile.getMemberLevel(member) * 2;
        db.setColumn(member.getId(), "exp", "" + (profile.getMemberExp(member) + expToGain));

        eb.addField("**Results**", "Woah, a shiny diamond?? \uD83D\uDC8E Shiny... \uD83D\uDCB8\uD83D\uDCB8\uD83D\uDCB8\n" +
                "You also gained " + (expToGain) + " experience.", false);
        int diamondAmount = Integer.parseInt(db.getColumn(member.getId(), "diamond"));
        db.setColumn(member.getId(), "diamond", "" + (diamondAmount + 1));
    }


    private void handleMythical(EmbedBuilder eb, Member member) {
        int expToGain = 200 + 10 * profile.getMemberLevel(member);
        db.setColumn(member.getId(), "exp", "" + (profile.getMemberExp(member) + expToGain));

        eb.setColor(Color.MAGENTA);
        eb.setDescription("You found a **mythical** item!!");

        int rand2 = (int) (Math.random() * 3);

        if (rand2 == 0) {

            eb.addField("**Results**", "A Mythical Knife \uD83D\uDD2A Only legends talked about these.\n" +
                    "You also gained " + (expToGain) + " experience.", false);
            db.setColumn(member.getId(), "mKnife", "" + (inventory.getMythicalKnife(member) + 1));

        } else if (rand2 == 1) {

            eb.addField("**Results**", "A Mythical Shield \uD83E\uDDFF No one will mess with you.\n" +
                    "You also gained " + (expToGain) + " experience.", false);
            db.setColumn(member.getId(), "mShield", "" + (inventory.getMythicalShield(member) + 1));


        } else if (rand2 == 2) {
            eb.addField("**Results**", "A Mythical Hourglass ⌛ This amulet let's you travel through time.\n" +
                    "You also gained " + (expToGain) + " experience.", false);
            db.setColumn(member.getId(), "hourglass", "" + (inventory.getHourglass(member) + 1));

        }
    }

    private void handleLegendary(MessageReceivedEvent event, EmbedBuilder eb, Member member) {
        eb.setColor(Color.BLACK);
        event.getChannel().sendMessage("legendary").queue();

        eb.setDescription("You found a **LEGENDARY** item!!");

        int expToGain = 1000 + 100 * profile.getMemberLevel(member);
        db.setColumn(member.getId(), "exp", "" + (profile.getMemberExp(member) + expToGain));

        eb.addField("**Results**", "You claimed A Legendary Amulet \uD83C\uDFC5 This will make you unstoppable in the long run.\n" +
                "You also gained " + (expToGain) + " experience.", false);
        int currAttack = Integer.parseInt(db.getColumn(member.getId(), "attack"));
        db.setColumn(member.getId(), "legAmulet", "" + (inventory.getLegendaryAmulet(member) + 1));
        db.setColumn(member.getId(), "amuletMult", "" + (inventory.getAmuletMuliplier(member) + 1));

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
