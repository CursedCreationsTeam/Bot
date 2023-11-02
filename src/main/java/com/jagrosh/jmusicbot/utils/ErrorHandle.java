package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.commands.minecraft.VersionReleasedCmd;
import net.dv8tion.jda.api.MessageBuilder;

import java.util.function.BiConsumer;

public class ErrorHandle {
    public static void errorHandle(CommandEvent event, Exception caughtException)
    {
        event.getChannel().sendMessage(new MessageBuilder().append(caughtException.getLocalizedMessage()).build()).queue();
        // Todo: Make this an embed
        if (VersionReleasedCmd.debug)
            for (StackTraceElement stackTraceElement : caughtException.getStackTrace())
                handleError(() -> event.getChannel().sendMessage(new MessageBuilder().append(stackTraceElement.toString()).build()).queue(), event);
    }
    public static Exception caughtException;
    public static void handleError(Runnable runAttempt, CommandEvent event, Runnable runWhenDone) {
        handleError(runAttempt, event);
        runWhenDone.run();
    }
    public static void handleError(Runnable runAttempt, CommandEvent event) {
        handleError(runAttempt, () -> {
            event.getChannel().sendMessage(new MessageBuilder().append(caughtException.toString()).build()).queue();
            if (VersionReleasedCmd.debug)
                for (StackTraceElement stackTraceElement : caughtException.getStackTrace())
                    handleError(() -> event.getChannel().sendMessage(new MessageBuilder().append(stackTraceElement.toString()).build()).queue(), event);
        });
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
