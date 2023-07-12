package com.jagrosh.jmusicbot.commands.minecraft;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.MessageBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Exception;
/**
 *
 * @author mikenotpike/SolDev69 <michaelcraft1104@gmail.com>
 */
public class VersionReleasedCmd extends Command {
    protected final HashMap<String, String> VERSIONMAP = new HashMap<>();

    public VersionReleasedCmd(Bot bot) {
        this.name = "version released";
        this.help = "When was {x} version released?";
        this.aliases = bot.getConfig().getAliases(this.name);
        try {
            VERSIONMAP.put("1.20.1", readJsonFromUrl("https://piston-meta.mojang.com/v1/packages/715ccf3330885e75b205124f09f8712542cbe7e0/1.20.1.json").get("releaseTime").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageBuilder builder = new MessageBuilder().append(event.getArgs() + " released on " + VERSIONMAP.get(event.getArgs()));
    }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
    }
    
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}