package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

public class ErrorHandle {
    public static void defaultRoC(CommandEvent event, Exception caughtException)
    {
        event.getChannel().sendMessage(new MessageBuilder().append((caughtException).toString()).build()).queue();
    }
    public static Exception caughtException;
    public static void handleError(Runnable runAttempt, CommandEvent event, Runnable runWhenDone) throws Exception {
        handleError(runAttempt, event);
        runWhenDone.run();
    }
    public static void handleError(Runnable runAttempt, CommandEvent event) {
        handleError(runAttempt, () -> event.getChannel().sendMessage(new MessageBuilder().append(caughtException.toString()).build()).queue());
    }
    public static void handleError(Runnable runAttempt, Runnable runOnCatch)  {
        try {
            runAttempt.run();
        } catch (Exception exception) {
            caughtException = exception;
            runOnCatch.run();
        }
    }
}
