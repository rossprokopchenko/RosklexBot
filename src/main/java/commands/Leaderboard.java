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
import java.text.DecimalFormat;
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

            String[] ids = Database.getDb().getTop("rank", "DESC");


            int size = ids.length;
            Map<String, Integer> totalExpTop = new HashMap<>();

            for(int i = 0; i < size; i++){
                int level = Integer.parseInt(Database.getDb().getColumn(ids[i], "level"));
                int exp = Integer.parseInt(Database.getDb().getColumn(ids[i], "exp"));
                int totalExp = 0;

                for(int ii = level; ii > 0; ii--){
                    totalExp += (50 * (int) Math.pow(ii - 3, 2) + 150 * (ii - 3) + 300);
                }
                totalExp += exp - 200;

                totalExpTop.put(ids[i], totalExp);
            }

            Map<String, Integer> sorted = totalExpTop
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));

            totalExpTop = sorted;
            ids = totalExpTop.keySet().toArray(new String[size]);

            String leaderboard = "";

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
                double amount = Double.parseDouble("" + totalExpTop.get(ids[i]));
                DecimalFormat formatter = new DecimalFormat("#,###");

                if(i == 0){
                    leaderboard += "**" + (i+1) + ". \uD83D\uDC79 " + names[i] + " - Level " + levels[i] + " - Total Exp: " + formatter.format(amount) + "**\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else if(i == 1){
                    leaderboard += (i+1) + ". \uD83D\uDC80 " + names[i] + " - *Level " + levels[i] + " - Total Exp: " + formatter.format(amount) + "*\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else if(i == 2){
                    leaderboard += (i+1) + ". \uD83E\uDD49 " + names[i] + " - *Level " + levels[i] + " - Total Exp: " + formatter.format(amount) + "*\n" +
                            "↳ *Total Dungeon Runs: " + dCounters[i] + "*\n\n";
                } else {
                    leaderboard += (i+1) + ". " + names[i] + " - *Level " + levels[i] + " - Total Exp: " + formatter.format(amount) + "*\n" +
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
