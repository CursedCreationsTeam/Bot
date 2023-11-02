package com.jagrosh.jmusicbot.commands.minecraft;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.ErrorHandle;
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
import java.util.concurrent.atomic.AtomicReference;

import static com.jagrosh.jmusicbot.utils.ErrorHandle.handleError;

/**
 * @author mikenotpike/SolDev69 <michaelcraft1104@gmail.com>
 */
public class VersionReleasedCmd extends Command {
    protected final HashMap<String, String> VERSIONMAP = new HashMap<>();

    public VersionReleasedCmd(Bot bot) {
        this.name = "version";
        this.help = "When was {x} version released?";
        this.aliases = bot.getConfig().getAliases(this.name);
        try {
            offload_stringFormat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void offload_stringFormat() throws IOException {
        List<Version> arrayList = offload_arrayFormat();
        arrayList.forEach(version -> {
            System.out.println("Throwing " + version.getId() + " and " + version.getUrl() + " into the map!");
            VERSIONMAP.put(version.getId(), version.getUrl());
        });
    }
    private List<Version> offload_arrayFormat() throws IOException {
        String jsonString = readJsonFromUrl("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").get("versions").toString();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Version> myObjects = null;
        try {
            myObjects = objectMapper.readValue(jsonString, new TypeReference<List<Version>>() {});
            for (Version obj : myObjects) {
                System.out.println(obj.getId());
                System.out.println(obj.getUrl());
                // Access other fields as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myObjects;
    }
    private static String parseTime(String unparsedTime) {
        // Parse the input string into an OffsetDateTime
        OffsetDateTime dateTime = OffsetDateTime.parse(unparsedTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Format the OffsetDateTime into the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' hh:mma");
        String formattedDate = dateTime.format(formatter);

        return formattedDate;
    }
    @Override
    protected void execute(CommandEvent event) {
        MessageBuilder builder = new MessageBuilder();
        try {
            builder = builder.append(event.getArgs()).append(" released on ").append(parseTime(readJsonFromUrl(VERSIONMAP.get(event.getArgs())).get("releaseTime").toString()));
        } catch (IOException e) {
            ErrorHandle.defaultRoC(event, e);
        }
        MessageBuilder finalBuilder = builder;
        handleError(() -> event.getChannel().sendMessage(finalBuilder.build()).queue(), event);
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

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(readJsonFromUrl("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").get("versions").toString().split("},")));
    }
}