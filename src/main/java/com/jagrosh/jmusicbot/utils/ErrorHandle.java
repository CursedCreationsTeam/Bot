package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;

public class ErrorHandle {
    public static void handleError(MessageBuilder builder, CommandEvent event, Runnable function) {
        try {
            event.getChannel().sendMessage(builder.build()).queue();
        } catch (IllegalStateException illegalStateException) {
            event.getChannel().sendMessage(new MessageBuilder().append(illegalStateException.toString()).build()).queue();
            function.run(); // I don't remember why I wanted to add the lambda run
        }
    }
}
