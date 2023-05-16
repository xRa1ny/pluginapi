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
    String permission();

    /**
     * defines if this command can only be executed by a player
     */
    boolean requiresPlayer();

    /**
     * defines all valid arguments of this command
     *
     * <p>
     * <br/>
     *
     * Supported patterns:
     * <li>%PLAYER%/li>
     * <li>%COMMAND%</li>
     * <li>%INTEGER%</li>
     * <li>%BOOLEAN%</li>
     *
     * <br />
     *
     * all other patterns will not be converted in auto tabcomplete!
     *
     * <p>
     * <br />
     *
     * <strong>might contain the following values:</strong>
     * <li>user %PLAYER% permission set %PERMISSION%</li>
     * <li>group %GROUP% create</li>
     * <li>user %PLAYER% allowed set %BOOLEAN%</li>
     * <li>user %PLAYER% rd set %INTEGER%</li>
     */
    @NotNull
    String[] args();
}
