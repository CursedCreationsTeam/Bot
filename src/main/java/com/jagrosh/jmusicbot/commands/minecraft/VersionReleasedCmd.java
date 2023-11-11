package com.jagrosh.jmusicbot.commands.minecraft;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.utils.ErrorHandle;
import net.dv8tion.jda.api.MessageBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.jagrosh.jmusicbot.utils.ErrorHandle.handleError;

/**
 * @author mikenotpike/SolDev69 <michaelcraft1104@gmail.com>
 */
public class VersionReleasedCmd extends Command {
    public static boolean debug = false;
    protected final HashMap<String, String> VERSIONMAP = new HashMap<>();
    public VersionReleasedCmd(Bot bot) {
        this.name = "version";
        this.help = "When was {x} version released?";
        this.aliases = bot.getConfig().getAliases(this.name);
        try {
            debug = JMusicBot.config.getGame().getName().equals("ALPHA TESTING");
        } catch (NullPointerException ignored) {}
    }

    private void offload_stringFormat() throws IOException {
        List<Version> arrayList = offload_arrayFormat();
        arrayList.forEach(version -> {
            System.out.println("Throwing " + version.getId() + " and " + version.getUrl() + " into the map!");
            VERSIONMAP.put(version.getId(), version.getUrl());
        });
        System.out.println();
    }
    private List<Version> offload_arrayFormat() throws IOException {
        String jsonString = readJsonFromUrl("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").get("versions").toString();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Version> myObjects = null;
        try {
            myObjects = objectMapper.readValue(jsonString, new TypeReference<List<Version>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myObjects;
    }
    private static String parseTime(String unparsedTime) {
        // Parse the input string into an OffsetDateTime
        OffsetDateTime dateTime = OffsetDateTime.parse(unparsedTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);;

        // Create date
        Date date = Date.from(dateTime.toInstant());

        // Format date as a string in the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mma z");
        String formattedDate = dateFormat.format(date);

        // Return the string
        return formattedDate;
    }

    private static String convertDateString(String originalDateString) {
        try {
            // Parse the original date string
            SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date date = originalFormat.parse(originalDateString);

            // Format the date as a string in the desired format
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd 'at' h:mma z", Locale.ENGLISH);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException as needed
            return null;
        }
    }


    private static String parseDate(Date date) {
        // Format date as a string in the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mma z");
        return dateFormat.format(date);
    }
    @Override
    protected void execute(CommandEvent event) {
        try {
            offload_stringFormat();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        MessageBuilder builder = new MessageBuilder();
        try {
            builder = builder.append(event.getArgs()).append(" released on ").append(parseTime(readJsonFromUrl("https://files.betacraft.uk/launcher/v2/assets/jsons/" + event.getArgs() + ".json").get("releaseTime").toString())).append(" and was compiled on " ).append(callBetacraftModRepo(event)[1]);
            MessageBuilder finalBuilder = builder;
            handleError(() -> event.getChannel().sendMessage(finalBuilder.build()).queue(), event);
        } catch (IOException e) {
            try {
                builder.clear();
                builder = builder.append(event.getArgs()).append(" released on ").append(parseTime(readJsonFromUrl(VERSIONMAP.get(event.getArgs())).get("releaseTime").toString()));
                try {
                    builder.append(" and was compiled on " ).append(convertDateString(callBetacraftModRepo(event)[1]));
                } catch (IOException ioException) {
                    builder.append(parseTime(readJsonFromUrl(VERSIONMAP.get(event.getArgs())).get("releaseTime").toString()));
                }
                MessageBuilder finalBuilder = builder;
                handleError(() -> event.getChannel().sendMessage(finalBuilder.build()).queue(), event);
            } catch (IOException ex) {
                event.getChannel().sendMessage("Sorry, this version could not be found :(").queue();
                if (debug)
                    ErrorHandle.errorHandle(event, e);
            }

        }
    }


    private String[] callBetacraftModRepo(CommandEvent event) throws IOException {
        String releaseTime = readFileFromUrl("https://files.betacraft.uk/launcher/assets/jsons/" + event.getArgs() + ".info").split("\n")[0].split(":")[1];
        Date releaseDate = new Date(Long.parseLong(releaseTime));
        String compileTime = readFileFromUrl("https://files.betacraft.uk/launcher/assets/jsons/" + event.getArgs() + ".info").split("\n")[1].split(":")[1];
        Date compileDate = new Date(Long.parseLong(compileTime));
        String[] dateStrings = new String[] { releaseTime.equals("0") ? "an unspecified date" : releaseDate.toString(), compileTime.equals("0") ? "an unspecified date" : compileDate.toString()};
        //Date[] dates = new Date[] {releaseDate, compileDate};
        return dateStrings;
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
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }
    public static String readFileFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return readAll(rd);
        }
    }
}
