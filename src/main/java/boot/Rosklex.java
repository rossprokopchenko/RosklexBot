package boot;

import config.Config;
import listeners.commands.*;
import listeners.events.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.JSONException;
import database.DatabaseManager;

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

        prefix = '!';

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

        JDA api = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(listening(prefix + "help commands"))
                .build();
        DatabaseManager db = new DatabaseManager(resourcePath, "scores");
        initializeListeners(db);

        for (Object obj : listeners) {
            api.addEventListener(obj);
        }
    }

    private static void initializeListeners(DatabaseManager db) {
        listeners = new ArrayList<>();

        NewUser newUser = new NewUser(db);
        RankFixer rankFixer = new RankFixer(db);

        Leaderboard leaderboard = new Leaderboard(db);
        Admin admin = new Admin(db);
        Help help = new Help();
        Profile profile = new Profile(db);
        Daily daily = new Daily(db, profile);
        Inventory inventory = new Inventory(db);
        Store store = new Store(db, profile, inventory);
        Dungeon dungeon = new Dungeon(db, profile);
        Mine mine = new Mine(db, profile, inventory);
        Info info = new Info();

        DungeonNotifier dungeonNotifier = new DungeonNotifier(db, dungeon);
        SkillFixer skillFixer = new SkillFixer(db, inventory);
        LevelUpNotifier levelUpNotifier = new LevelUpNotifier(db, profile);

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

    }

    public static char getPrefix() {
        return prefix;
    }
}
