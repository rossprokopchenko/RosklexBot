package com.rosklex.boot;

import com.rosklex.config.Config;
import com.rosklex.database.DatabaseTypes;
import com.rosklex.listeners.commands.*;
import com.rosklex.listeners.events.*;
import com.rosklex.listeners.slash.HelpSlash;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.JSONException;
import com.rosklex.database.DatabaseManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.entities.Activity.listening;

@Slf4j
public class Rosklex {
    public static char prefix;
    private static boolean devMode = false;

    private static List<Object> listeners = null;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please specify a resource folder path.");
            return;
        }

        prefix = '%';

        if (args.length > 1) {
            if (args[1].equals("dev")) {
                devMode = true;
                prefix = '&';
            }
        }

        String resourcePath = args[0];
        log.info("Loading resources from " + resourcePath);

        run(resourcePath);
    }

    private static void run(String resourcePath) throws LoginException, JSONException, IOException {
        Config config = new Config(new File(resourcePath + "/botconfig.json"));
        String token = config.getString("token");

        if (devMode) token = config.getString("devToken");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.listening(prefix + "prefix | \uD83D\uDE42"));
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        ShardManager api = builder.build();

        ArrayList<String> dbFilenames = new ArrayList<>();
        dbFilenames.add(DatabaseTypes.SCORES_DB_NAME);
        dbFilenames.add(DatabaseTypes.SERVERS_DB_NAME);

        DatabaseManager dbMan = new DatabaseManager(resourcePath, dbFilenames);
        initializeListeners(dbMan);

        for (Object obj : listeners) {
            api.addEventListener(obj);
        }
    }

    private static void initializeListeners(DatabaseManager dbMan) {
        listeners = new ArrayList<>();

        ServerInfoUpdater serverInfoUpdater = new ServerInfoUpdater(dbMan);
        RosklexMessage rosklexMessage = new RosklexMessage(dbMan);

        Prefix prefix = new Prefix(dbMan);

        Stfu stfu = new Stfu();
        Stats stats = new Stats(dbMan);
        NewUser newUser = new NewUser(dbMan);
        RankFixer rankFixer = new RankFixer(dbMan);

        Leaderboard leaderboard = new Leaderboard(dbMan);
        Admin admin = new Admin(dbMan);
        Help help = new Help(dbMan);
        Profile profile = new Profile(dbMan);
        Daily daily = new Daily(dbMan, profile);
        Inventory inventory = new Inventory(dbMan);
        Store store = new Store(dbMan, profile, inventory);

        LevelUpNotifier levelUpNotifier = new LevelUpNotifier(dbMan, profile);

        Dungeon dungeon = new Dungeon(dbMan, profile, levelUpNotifier);
        Mine mine = new Mine(dbMan, profile, inventory);
        Info info = new Info(dbMan);
        Invite invite = new Invite();

        DungeonNotifier dungeonNotifier = new DungeonNotifier(dbMan, dungeon);
        SkillFixer skillFixer = new SkillFixer(dbMan, inventory);

        HelpSlash helpSlash = new HelpSlash();

        listeners.add(prefix);
        listeners.add(rosklexMessage);
        listeners.add(newUser);
        listeners.add(skillFixer);
        listeners.add(rankFixer);
        listeners.add(dungeonNotifier);
        listeners.add(levelUpNotifier);
        listeners.add(leaderboard);
        listeners.add(admin);
        listeners.add(help);
        listeners.add(daily);
        listeners.add(profile);
        listeners.add(inventory);
        listeners.add(store);
        listeners.add(dungeon);
        listeners.add(mine);
        listeners.add(info);
        listeners.add(invite);

        listeners.add(helpSlash);

        listeners.add(stats);
        listeners.add(stfu);
        listeners.add(serverInfoUpdater);

    }

    public static char getPrefix() {
        return prefix;
    }
}
