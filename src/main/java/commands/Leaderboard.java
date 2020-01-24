package commands;

import main.Rosklex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import javax.annotation.Nonnull;
import javax.xml.crypto.Data;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        String[] message = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMessage().getMember();

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.PREFIX) {
            return;
        }

        message[0] = message[0].substring(1, message[0].length());


        if(message[0].equalsIgnoreCase("top")){

            Color gold = new Color(255,215,0);
            Color silver = new Color(192,192,192);
            Color bronze = new Color(	205, 127, 50);

            String[] ids = Database.getDb().getTop("rank", "ASC");

            String leaderboard = "";

            int size = ids.length;
            String[] names = new String[size];
            String[] levels = new String[size];
            String[] exps = new String[size];
            String[] dCounters = new String[size];

            for(int i = 0; i < ids.length; i++){
                names[i] = Database.getDb().getColumn(ids[i], "name");
                levels[i] = Database.getDb().getColumn(ids[i], "level");
                exps[i] = Database.getDb().getColumn(ids[i], "exp");
                dCounters[i] = Database.getDb().getColumn(ids[i], "dCounter");
            }


            // left off where the map is unsorted, need to store name and level in descending sort

            Date date = new Date();
            EmbedBuilder eb = new EmbedBuilder();

            int start = 0;

            eb.setColor(gold);
            eb.setFooter("Leaderboard Page 1", member.getUser().getAvatarUrl());

            if(message.length == 2 && message[1].equals("2")){
                start = 5;
                eb.setColor(silver);
                eb.setFooter("Leaderboard Page 2", member.getUser().getAvatarUrl());
            } else if(message.length == 2 && message[1].equals("3")){
                start = 10;
                eb.setColor(bronze);
                eb.setFooter("Leaderboard Page 3", member.getUser().getAvatarUrl());
            }

            for(int i = start; i < start + 5; i++){
                if(i == 0){
                    leaderboard += "**" + (i+1) + ". \uD83D\uDC79 " + names[i] + " - Level " + levels[i] + " Exp " + exps[i] + "**\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else if(i == 1){
                    leaderboard += (i+1) + ". \uD83D\uDC80 " + names[i] + " - *Level " + levels[i] + " Exp " + exps[i] + "*\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else if(i == 2){
                    leaderboard += (i+1) + ". \uD83E\uDD49 " + names[i] + " - *Level " + levels[i] + " Exp " + exps[i] + "*\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else {
                    leaderboard += (i+1) + ". " + names[i] + " - *Level " + levels[i] + " Exp " + exps[i] + "*\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                }
            }


            eb.setAuthor("\uD83C\uDFC6 Leaderboard \uD83C\uDFC6");
            eb.setTimestamp(date.toInstant());
            eb.setDescription(leaderboard);

            e.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
