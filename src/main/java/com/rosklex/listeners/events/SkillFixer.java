package com.rosklex.listeners.events;

import com.rosklex.listeners.commands.Inventory;
import com.rosklex.listeners.commands.Store;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.rosklex.database.DatabaseManager;

public class SkillFixer extends ListenerAdapter {
    int totalAttack = 0;
    int totalDefence = 0;
    int totalSwiftness = 0;

    private DatabaseManager db;
    private Inventory inventory;

    public SkillFixer(DatabaseManager db, Inventory inventory) {
        this.db = db;
        this.inventory = inventory;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        totalAttack = 0;
        totalDefence = 0;
        totalSwiftness = 0;

        totalAttack += Store.stickAttack * inventory.getStick(member);
        totalAttack += Store.hammerAttack * inventory.getHammer(member);
        totalAttack += Store.daggerAttack * inventory.getDagger(member);
        totalAttack += Store.gunAttack * inventory.getGun(member);
        totalAttack += Store.mKnifeAttack * inventory.getMythicalKnife(member);

        totalDefence += Store.orbDefence * inventory.getProtectionOrb(member);
        totalDefence += Store.shieldDefence * inventory.getShield(member);
        totalDefence += Store.mShieldDefence * inventory.getMythicalShield(member);

        totalSwiftness += Store.bootsSwiftness * inventory.getBoots(member);
        totalSwiftness += Store.hourglassSwiftness * inventory.getHourglass(member);

        setSkill(member, totalAttack, totalDefence, totalSwiftness);
    }

    private void setSkill(Member member, int attack, int defence, int swiftness) {
        db.setColumn(member.getId(), "attack", "" + (attack * inventory.getAmuletMuliplier(member)), "scores");
        db.setColumn(member.getId(), "defence", "" + (defence * inventory.getAmuletMuliplier(member)), "scores");
        db.setColumn(member.getId(), "swiftness", "" + (swiftness * inventory.getAmuletMuliplier(member)), "scores");
    }
}
