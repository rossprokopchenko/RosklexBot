package listeners.commands;

import listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import database.DatabaseManager;

import java.awt.*;
import java.util.Date;

public class Inventory extends ListenerAdapter {
    private DatabaseManager db;

    public Inventory(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("inventory") || message[0].equalsIgnoreCase("inv")) {

            if (message.length == 2) {
                try {
                    member = event.getMessage().getMentions().getMembers().get(0);
                } catch (Exception ex) {

                }
            }

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setAuthor("\uD83C\uDF92 " + member.getUser().getName() + "'s inventory");

            // WEAPONS
            String weapons = "";


            if (getStick(member) > 0) {
                weapons += "\uD83E\uDDAF **Walking Stick** x" + getStick(member) + " (" + Store.stickAttack * getStick(member) + " Attack) : Are you blind or something?.\n";
            }
            if (getHammer(member) > 0) {
                weapons += "\uD83D\uDD28 **A Handyman's Hammer** x" + getHammer(member) + " (" + Store.hammerAttack * getHammer(member) + " Attack) : You can throw it, or not.\n";

            }
            if (getDagger(member) > 0) {
                weapons += "\uD83D\uDDE1 **Dagger** x" + getDagger(member) + " (" + Store.daggerAttack * getDagger(member) + " Attack) : Stab... Stab... Stab...\n";

            }
            if (getGun(member) > 0) {
                weapons += "\uD83D\uDD2B **Paintball Gun** x" + getGun(member) + " (" + Store.gunAttack * getGun(member) + " Attack) : Just don\'t aim for the head.\n";

            }
            if (getMythicalKnife(member) > 0) {
                weapons += "\uD83D\uDD2A **Mythical Knife** x" + getMythicalKnife(member) + " (" + Store.mKnifeAttack * getMythicalKnife(member) + " Attack) : Be careful with this thing.\n";

            }

            // DEFENCE
            String defence = "";
            if (getProtectionOrb(member) > 0) {
                defence += "\uD83D\uDD2E **Protection Orb** x" + getProtectionOrb(member) + " (" + Store.orbDefence * getProtectionOrb(member) + " Defence) : Orbits your character and grants protection.\n";

            }
            if (getShield(member) > 0) {
                defence += "\uD83D\uDEE1 **Fighter's Shield** x" + getShield(member) + " (" + Store.shieldDefence * getShield(member) + " Defence) : Help's you in close combat.\n";

            }
            if (getMythicalShield(member) > 0) {
                defence += "\uD83E\uDDFF **Mythical Shield** x" + getMythicalShield(member) + " (" + Store.mShieldDefence * getMythicalShield(member) + " Defence) : It's very light but durable.\n";

            }

            // MOBILITY
            String mobility = "";
            if (getBoots(member) > 0) {
                mobility += "\uD83D\uDC5F **Mobility Boots** x" + getBoots(member) + " (" + Store.bootsSwiftness * getBoots(member) + " Swiftness) : Let's you complete dungeon runs faster..\n";

            }

            // Miscellaneous items

            String misc = "";

            if (getPickDurability(member) > 0) {
                misc += "⛏ **Pickaxe** (" + getPickDurability(member) + " uses): A standard pickaxe that lets you mine.\n";
            }
            if (getJunk(member) > 0) {
                misc += "⚙ \uD83D\uDD27 \uD83D\uDD11 \uD83D\uDD0B Junk x" + getJunk(member) + "\n";
            }
            if (getMoneyBags(member) > 0) {
                misc += "\uD83D\uDCB0 Bag of Coins x" + getMoneyBags(member) + "\n";
            }
            if (getDiamonds(member) > 0) {
                misc += "\uD83D\uDC8E Diamond x" + getDiamonds(member) + "\n";
            }

            // AMULETS

            String amulets = "";

            if (getLegendaryAmulet(member) > 0) {
                amulets += "\uD83C\uDFC5 **Legendary Amulet** (" + getLegendaryAmulet(member) + "): Adds 1 to skill multiplier.\n";
            }
            if (getHourglass(member) > 0) {
                amulets += "⌛ **Mythical Hourglass** x" + getHourglass(member) + " (" + Store.hourglassSwiftness * getHourglass(member) + " Swiftness): This amulet let's you travel through time.\n";

            }

            // STRING ADD

            if (!amulets.equals("")) {
                eb.addField("Amulets", amulets, false);
            }

            if (!weapons.equals("")) {
                eb.addField("Weapons",
                        weapons, false);
            }

            if (!defence.equals("")) {
                eb.addField("Defence Pieces", defence, false);
            }

            if (!mobility.equals("")) {
                eb.addField("Accessories", mobility, false);
            }

            if (!misc.equals("")) {
                eb.addField("Miscellaneous", misc, false);
            }


            if (weapons.equals("") && misc.equals("") && amulets.equals("")) {
                eb.addField("", member.getUser().getName() + "'s inventory is empty :(", false);
            }


            eb.addField("", "*Overall boost to stats from items* : +" + getTotalAttack(member) + " Attack, +" +
                    "" + getTotalDefence(member) + " Defence, +" + getTotalSwiftness(member) + " Swiftness, Total Skill Multiplier x" + getAmuletMuliplier(member), false);
            eb.addField("Use **sell [item]** to sell mine loot, or **sell mine all**", "", false);

            eb.setFooter(member.getUser().getName() + "'s inventory", member.getUser().getAvatarUrl());
            eb.setTimestamp(date.toInstant());

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }


    public int getTotalAttack(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "attack")) / getAmuletMuliplier(member);
    }

    public int getTotalDefence(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "defence")) / getAmuletMuliplier(member);
    }

    public int getTotalSwiftness(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "swiftness")) / getAmuletMuliplier(member);
    }


    public int getStick(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "stick"));
    }

    public int getHammer(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "hammer"));
    }

    public int getDagger(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "dagger"));
    }

    public int getGun(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "gun"));
    }

    public int getMythicalKnife(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "mKnife"));
    }

    public int getMythicalShield(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "mShield"));
    }

    public int getHourglass(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "hourglass"));
    }

    public int getLegendaryAmulet(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "legAmulet"));
    }

    public int getPickDurability(Member member) {
        return (int) (Integer.parseInt(db.getColumn(member.getId(), "pickaxe")));
    }

    public int getJunk(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "gear")) +
                Integer.parseInt(db.getColumn(member.getId(), "wrench")) +
                Integer.parseInt(db.getColumn(member.getId(), "rustyKey")) +
                Integer.parseInt(db.getColumn(member.getId(), "battery"));
    }

    public int getMoneyBags(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "moneyBag"));
    }

    public int getDiamonds(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "diamond"));
    }

    public int getAmuletMuliplier(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "amuletMult"));
    }

    public int getProtectionOrb(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "orb"));
    }

    public int getShield(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "shield"));
    }

    public int getBoots(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "boots"));
    }


}
