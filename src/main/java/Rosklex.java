import commands.*;
import config.Config;
import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import sqlite.Database;

import javax.security.auth.login.LoginException;
import java.io.File;

import static net.dv8tion.jda.api.entities.Activity.listening;

public class Rosklex {
    public static final char PREFIX = '!';

    public static void main(String[] args) throws Exception {
        Config config = new Config(new File("botconfig.json"));

        run(config);
    }

    private static void run(Config config) throws LoginException {
        JDA api = JDABuilder.createLight(config.getString("token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(listening(PREFIX + "help commands"))
                .build();
        //JDA jda = new JDABuilder(config.getString("token")).setActivity(listening(" !help for commands")).build();

        Database.getDb().run();

        api.addEventListener(new NewUser());
        api.addEventListener(new SkillFixer());
        api.addEventListener(new RankFixer());
        api.addEventListener(new Leaderboard());
        api.addEventListener(new Admin());
        api.addEventListener(new Help());
        api.addEventListener(new Daily());
        api.addEventListener(new Profile());
        api.addEventListener(new Inventory());
        api.addEventListener(new Store());
        api.addEventListener(new Dungeon());
        api.addEventListener(new Mine());
        api.addEventListener(new Info());
        api.addEventListener(new DungeonListener());
        api.addEventListener(new LevelListener());

    }
}
