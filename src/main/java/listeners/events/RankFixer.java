package listeners.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import database.DatabaseManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RankFixer extends ListenerAdapter {
    private DatabaseManager db;

    public RankFixer(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isRosklexMessage = RosklexMessage.isRosklexMessage(event.getMessage());

        if (!isRosklexMessage) return;

        String[] message = event.getMessage().getContentRaw().split(" ");
        Member member = event.getMessage().getMember();

        String[] ids = db.getTop("name", "DESC");

        int size = ids.length;
        Map<String, Integer> totalExpTop = new HashMap<>();

        for(int i = 0; i < size; i++){
            int level = Integer.parseInt(db.getColumn(ids[i], "level"));
            int exp = Integer.parseInt(db.getColumn(ids[i], "exp"));
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

        for(int i = 0; i < totalExpTop.size(); i++){
            totalExpTop.replace(ids[i], i+1);
            setRank(ids[i], i+1);
        }
    }

    private void setRank(String id, int rank){
        db.setColumn(id, "rank", "" + rank);
    }
}
