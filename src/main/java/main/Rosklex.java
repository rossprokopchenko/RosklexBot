package main;

import commands.*;
import config.Config;
import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.JSONException;
import sqlite.Database;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

import static net.dv8tion.jda.api.entities.Activity.listening;

public class Rosklex {
    public static char PREFIX = '!';
    private static boolean devMode = false;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please specify a resource folder path.");
            return;
        }

        if (args.length > 1) {
            if (args[1].equals("dev")) {
                devMode = true;
                PREFIX = '&';
            }
        }

        String resourcePath = args[0];
        System.out.println("Loading resources from path: " + resourcePath);

        run(resourcePath);
    }

    private static void run(String resourcePath) throws LoginException, JSONException, IOException {
        Config config = new Config(new File(resourcePath + "/botconfig.json"));
        String token = config.getString("token");

        if (devMode) token = config.getString("devToken");

        JDA api = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(listening(PREFIX + "help commands"))
                .build();
        Database.getDb().run(resourcePath);

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
