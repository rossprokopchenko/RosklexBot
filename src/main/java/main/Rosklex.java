package main;

import commands.*;
import config.Config;
import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import sqlite.Database;

import java.io.File;

import static net.dv8tion.jda.api.entities.Activity.listening;
import static net.dv8tion.jda.api.entities.Activity.playing;

public class Rosklex {
    public static final char PREFIX = '!';
    public static void main(String[] args) throws Exception{
        Config config = new Config(new File("botconfig.json"));

        JDA jda = new JDABuilder(config.getString("token")).setActivity(listening(" !help commands")).build();

        Database.getDb().run();

        jda.addEventListener(new NewUser());

        jda.addEventListener(new SkillFixer());

        jda.addEventListener(new Admin());
        jda.addEventListener(new Help());
        jda.addEventListener(new Daily());
        jda.addEventListener(new Profile());
        jda.addEventListener(new Inventory());
        jda.addEventListener(new Store());
        jda.addEventListener(new Dungeon());
        jda.addEventListener(new Mine());

        jda.addEventListener(new DungeonListener());
        jda.addEventListener(new LevelListener());

    }
}
