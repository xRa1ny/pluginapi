package me.xra1ny.pluginapi.commands;

import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.models.command.CommandInfo;
import me.xra1ny.pluginapi.models.command.CommandReturnState;
import me.xra1ny.pluginapi.models.command.RCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@CommandInfo(
        name = "reloadnonmysqlvalues",
        permission = "pluginapi.command.reloadnonmysqlvalues"
)
public class ReloadNonMysqlValuesCommand extends RCommand {
    @Override
    protected @NotNull CommandReturnState executeBaseCommand(@NotNull CommandSender sender) {
        RPlugin.sendMessage(sender, "Attempting to reload non mysql values...");
        RPlugin.getInstance().reloadNonMySqlValues();
        RPlugin.sendMessage(sender, "Successfully reloaded non mysql values!");

        return CommandReturnState.SUCCESS;
    }

    @Override
    protected @NotNull @Unmodifiable List<String> help(@NotNull CommandSender sender) {
        return List.of();
    }

    @Override
    public @NotNull List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull String args) {
        return List.of();
    }
}
