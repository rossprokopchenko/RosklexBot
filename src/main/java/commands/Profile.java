package commands;

import main.Rosklex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import java.awt.*;
import java.util.Date;

public class Profile extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMessage().getMember();

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.PREFIX) {
            return;
        }

        message[0] = message[0].substring(1, message[0].length());

        if(message[0].equalsIgnoreCase("profile")){
            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTimestamp(date.toInstant());

            if(message.length == 2){
                try {
                    member =  e.getMessage().getMentionedMembers().get(0);
                } catch (Exception ex){

                }
            }

            String rank = "" + getMemberRank(member);
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
                    "\n \uD83C\uDFC3\u200D Swiftness: **" + getMemberSwiftness(member)  + "**" +
                            "\n *Total Dungeon Runs: " + getMemberDungeonRuns(member) + "*", false);
            eb.addField("**Leaderboard Stats**", rank, false);

            if(Daily.dailyAvailable(member)){
                eb.addField("", "**Your daily is available!**", false);
            }

            eb.setFooter(member.getUser().getName() + "'s profile", member.getUser().getAvatarUrl());

            e.getChannel().sendMessage(eb.build()).queue();
        }

    }

    public static int getMemberLevel(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "level"));
    }

    public static int getMemberExp(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "exp"));
    }

    public static int getMemberCoins(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "coins"));
    }

    public static long getMemberDaily(Member member){
        return Long.parseLong(Database.getDb().getColumn(member.getId(), "lastDaily"));
    }

    public static int getMemberAttack(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "attack"));
    }

    public static int getMemberDefence(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "defence"));
    }

    public static int getMemberSwiftness(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "swiftness"));
    }

    public static int getMemberDungeonRuns(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "dCounter"));
    }

    public static int getMemberRank(Member member){
        return Integer.parseInt(Database.getDb().getColumn(member.getId(), "rank"));
    }

}
