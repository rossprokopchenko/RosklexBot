package listeners.commands;

import listeners.events.RosklexMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import database.DatabaseManager;

import java.awt.*;
import java.util.Date;

public class Profile extends ListenerAdapter {
    private DatabaseManager db;

    public Profile(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        message[0] = message[0].substring(1, message[0].length());

        if (message[0].equalsIgnoreCase("profile")) {
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTimestamp(date.toInstant());

            if (message.length == 2) {
                try {
                    member = event.getMessage().getMentions().getMembers().get(0);
                } catch (Exception ex) {

                }
            }

            String rank = "";
            if(getMemberRank(member) > 25){
                rank = "Rank 25+";
                eb.setAuthor("\uD83D\uDDA5 " + member.getUser().getName() + "'s Profile");
            } else if(getMemberRank(member) == 1){
                rank = "\uD83D\uDC79 **Rank 1**";
                eb.setAuthor("\uD83D\uDC79 " + member.getUser().getName() + "'s Profile");
            } else if(getMemberRank(member) == 2){
                rank = "\uD83D\uDC80 *Rank 2*";
                eb.setAuthor("\uD83D\uDC80 " + member.getUser().getName() + "'s Profile");
            } else if(getMemberRank(member) == 3){
                rank = "\uD83E\uDD49 *Rank 3*";
                eb.setAuthor("\uD83E\uDD49 " + member.getUser().getName() + "'s Profile");
            } else {
                rank = "Rank " + getMemberRank(member);
                eb.setAuthor("\uD83D\uDDA5 " + member.getUser().getName() + "'s Profile");
            }


            eb.addField("**Bot Stats**",
                    "⏫ Level: **" + getMemberLevel(member)
                            + "**\n\uD83C\uDF1F Experience: **" + getMemberExp(member) + "**", true);
            eb.addField("", "\uD83D\uDCC0 Coins: **" + getMemberCoins(member) + "**", true);

            eb.addField("**Dungeon Stats**",
                    "⚔ Attack: **" + getMemberAttack(member) + "**" +
                            "\n \uD83D\uDEE1 Defence: **" + getMemberDefence(member) + "**" +
                            "\n \uD83C\uDFC3\u200D Swiftness: **" + getMemberSwiftness(member) + "**" +
                            "\n *Total Dungeon Runs: " + getMemberDungeonRuns(member) + "*", false);
            eb.addField("**Leaderboard Stats**", rank, false);

            eb.setFooter(member.getUser().getName() + "'s profile", member.getUser().getAvatarUrl());

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public int getMemberLevel(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "level"));
    }

    public int getMemberExp(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "exp"));
    }

    public int getMemberCoins(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "coins"));
    }

    public long getMemberDaily(Member member) {
        return Long.parseLong(db.getColumn(member.getId(), "lastDaily"));
    }

    public int getMemberAttack(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "attack"));
    }

    public int getMemberDefence(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "defence"));
    }

    public int getMemberSwiftness(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "swiftness"));
    }

    public int getMemberDungeonRuns(Member member) {
        return Integer.parseInt(db.getColumn(member.getId(), "dCounter"));
    }

    public int getMemberRank(Member member){
        return Integer.parseInt(db.getColumn(member.getId(), "rank"));
    }

}
