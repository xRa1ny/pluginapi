package me.xra1ny.pluginapi.models.command;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    /**
     * defines the name of this command, excluding the slash (/)
     */
    @NotNull
    String name();

    /**
     * defines the permission required to run this command
     */
    @NotNull
    String permission() default "";

    /**
     * defines if this command can only be executed by a player
     */
    boolean requiresPlayer() default true;

    /**
     * defines all valid arguments of this command
     *
     * <p>
     * <br/>
     *
     * Supported patterns:
     * <li>%PLAYER%/li>
     * <li>%BOOLEAN%</li>
     * <li>%NUMBER%</li>
     *
     * <br />
     *
     * all other patterns will not be converted in auto tabcomplete!
     */
    @NotNull
    CommandArg[] args() default {};

    boolean localised() default false;
}
