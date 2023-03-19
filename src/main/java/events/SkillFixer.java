package events;

import commands.Inventory;
import commands.Store;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

public class SkillFixer extends ListenerAdapter {
    int totalAttack = 0;
    int totalDefence = 0;
    int totalSwiftness = 0;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        totalAttack = 0;
        totalDefence = 0;
        totalSwiftness = 0;

        totalAttack += Store.stickAttack * Inventory.getStick(member);
        totalAttack += Store.hammerAttack * Inventory.getHammer(member);
        totalAttack += Store.daggerAttack * Inventory.getDagger(member);
        totalAttack += Store.gunAttack * Inventory.getGun(member);
        totalAttack += Store.mKnifeAttack * Inventory.getMythicalKnife(member);

        totalDefence += Store.orbDefence * Inventory.getProtectionOrb(member);
        totalDefence += Store.shieldDefence * Inventory.getShield(member);
        totalDefence += Store.mShieldDefence * Inventory.getMythicalShield(member);

        totalSwiftness += Store.bootsSwiftness * Inventory.getBoots(member);
        totalSwiftness += Store.hourglassSwiftness * Inventory.getHourglass(member);

        setSkill(member, totalAttack, totalDefence, totalSwiftness);
    }

    public static int getTotalAttack(Member member) {
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "attack")) / Inventory.getAmuletMuliplier(member);
    }

    public static int getTotalDefence(Member member) {
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "defence")) / Inventory.getAmuletMuliplier(member);
    }

    public static int getTotalSwiftness(Member member) {
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "swiftness")) / Inventory.getAmuletMuliplier(member);
    }

    private void setSkill(Member member, int attack, int defence, int swiftness) {
        Database.getDb().setColumn(member.getId(), "attack", "" + (attack * Inventory.getAmuletMuliplier(member)));
        Database.getDb().setColumn(member.getId(), "defence", "" + (defence * Inventory.getAmuletMuliplier(member)));
        Database.getDb().setColumn(member.getId(), "swiftness", "" + (swiftness * Inventory.getAmuletMuliplier(member)));
    }
}
