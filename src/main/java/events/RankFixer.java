package events;

import main.Rosklex;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sqlite.Database;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RankFixer extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMessage().getMember();

        if (member.getUser().isBot() || message[0].charAt(0) != Rosklex.PREFIX) {
            return;
        }

        String[] ids = Database.getDb().getTop("name", "DESC");

        int size = ids.length;
        Map<String, Integer> totalExpTop = new HashMap<>();

        for(int i = 0; i < size; i++){
            int level = Integer.parseInt(Database.getDb().getColumn(ids[i], "level")) - 3;
            int exp = Integer.parseInt(Database.getDb().getColumn(ids[i], "exp"));
            totalExpTop.put(ids[i], (50 * (int) Math.pow(level, 2) + 150 * (level) + 300) + exp);

        }

        Map<String, Integer> sorted = totalExpTop
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        totalExpTop = sorted;
        ids = totalExpTop.keySet().toArray(new String[size]);

        for(int i = 0; i < totalExpTop.size(); i++){
            totalExpTop.replace(ids[i], i+1);
            setRank(ids[i], i+1);
        }
    }

    private void setRank(String id, int rank){
        Database.getDb().setColumn(id, "rank", "" + rank);
    }
}
