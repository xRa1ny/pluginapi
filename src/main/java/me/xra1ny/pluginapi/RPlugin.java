package me.xra1ny.pluginapi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.xra1ny.pluginapi.models.cloudnet.CloudNetManager;
import me.xra1ny.pluginapi.models.color.HexCodeManager;
import me.xra1ny.pluginapi.models.command.CommandManager;
import me.xra1ny.pluginapi.models.config.ConfigManager;
import me.xra1ny.pluginapi.models.config.RConfig;
import me.xra1ny.pluginapi.models.hologram.Hologram;
import me.xra1ny.pluginapi.models.hologram.HologramManager;
import me.xra1ny.pluginapi.models.item.ItemStackManager;
import me.xra1ny.pluginapi.models.listener.ListenerManager;
import me.xra1ny.pluginapi.models.localisation.LocalisationManager;
import me.xra1ny.pluginapi.models.maintenance.ServerMaintenanceManager;
import me.xra1ny.pluginapi.models.scoreboard.ScoreboardManager;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.models.user.RUserManager;
import me.xra1ny.pluginapi.models.user.UserInputWindowManager;
import me.xra1ny.pluginapi.utils.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public abstract class RPlugin extends JavaPlugin {
    /**
     * the singleton instance access point of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private static RPlugin instance;

    private boolean maintenanceEnabled;

    private String maintenanceMessage;

    /**
     * the config setting whether to use mysql or not
     */
    @Getter
    private boolean mysqlEnabled;

    @Getter
    private boolean forcePrefix;

    /**
     * the global prefix of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String prefix;

    @Getter
    private boolean forceChatColor;

    /**
     * the global chat color of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private ChatColor chatColor;

    @Getter
    private boolean forceCommandErrorMessage;

    /**
     * the global message to display when an error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandErrorMessage;

    @Getter
    private boolean forceCommandInternalErrorMessage;

    /**
     * the global message to display when an internal error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandInternalErrorMessage;

    @Getter
    private boolean forceCommandInvalidArgsErrorMessage;

    /**
     * the global message to display when a command has been passed invalid arguments
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandInvalidArgsErrorMessage;

    @Getter
    private boolean forcePlayerNoPermissionErrorMessage;

    /**
     * the global error message displayed when a player does not have the permissions to perform an action
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String playerNoPermissionErrorMessage;

    @Getter
    private boolean forcePlayerNotFoundErrorMessage;

    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String playerNotFoundErrorMessage;

    @Getter
    private boolean forceCommandOnlyPlayerErrorMessage;

    /**
     * the global error message displayed when the console performs a command that can only be executed by a player
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandOnlyPlayerErrorMessage;

    @Getter
    private boolean forceUserTimeout;

    private long userTimeout;

    @Getter(onMethod = @__(@NotNull))
    private ListenerManager listenerManager;

    /**
     * the command manager responsible for storing and managing all commands of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private CommandManager commandManager;

    /**
     * the user manager responsible for storing and managing all users of this plugin
     */
    private RUserManager userManager;

    /**
     * the maintenance manager responsible for storing all maintenance related information and managing them
     */
    @Getter(onMethod = @__(@NotNull))
    private ServerMaintenanceManager serverMaintenanceManager;

    /**
     * the item stack manager responsible for storing and managing all custom item stacks of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private ItemStackManager itemStackManager;

    /**
     * the scoreboard manager responsible for storing and managing all custom scoreboards of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private ScoreboardManager scoreboardManager;

    /**
     * the user input window manager responsible for storing and managing all user input windows of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private UserInputWindowManager userInputWindowManager;

    /**
     * the hologram manager responsible for storing and managing all holograms of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private HologramManager hologramManager;

    /**
     * the hex code manager responsible for creating strings with color gradients of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private HexCodeManager hexCodeManager;

    /**
     * the cloud net manager responsible for cloud net operations
     */
    @Getter(onMethod = @__(@NotNull))
    private CloudNetManager cloudNetManager;

    @Getter(onMethod = @__(@NotNull))
    private LocalisationManager localisationManager;

    @Getter(onMethod = @__(@NotNull))
    private ConfigManager configManager;

    /**
     * the time of plugin initialisation measured in milliseconds
     */
    @Getter
    private long startupTime;

    /**
     * called when this plugin enables
     */
    public abstract void onPluginEnable() throws Exception;

    /**
     * called when this plugin disables
     */
    public abstract void onPluginDisable() throws Exception;

    private void setupConfig() {
        getLogger().log(Level.INFO, "attempting to setup config...");

        if(!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }

        getLogger().setLevel(Level.parse(getConfig().getString(ConfigKeys.LOGGING_LEVEL, "ALL")));
        getConfig().set(ConfigKeys.LOGGING_LEVEL, getLogger().getLevel().toString());

        this.maintenanceEnabled = getConfig().getBoolean(ConfigKeys.MAINTENANCE_ENABLED, false);
        getConfig().set(ConfigKeys.MAINTENANCE_ENABLED, this.maintenanceEnabled);

        this.maintenanceMessage = getConfig().getString(ConfigKeys.MAINTENANCE_MESSAGE, "§lMaintenance!");
        getConfig().set(ConfigKeys.MAINTENANCE_MESSAGE, this.maintenanceMessage);

        this.mysqlEnabled = getConfig().getBoolean(ConfigKeys.MYSQL_ENABLED, false);
        getConfig().set(ConfigKeys.MYSQL_ENABLED, this.mysqlEnabled);

        this.forcePrefix = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PREFIX_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PREFIX_FORCE, this.forcePrefix);

        if(!this.mysqlEnabled || this.forcePrefix) {
            this.prefix = getConfig().getString(ConfigKeys.NON_MYSQL_PREFIX_VALUE, "§lMyPlugin  ");
            getConfig().set(ConfigKeys.NON_MYSQL_PREFIX_VALUE, this.prefix);
        }

        this.forceChatColor = getConfig().getBoolean(ConfigKeys.NON_MYSQL_CHAT_COLOR_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_CHAT_COLOR_FORCE, this.forceChatColor);

        if(!this.mysqlEnabled || this.forceChatColor) {
            this.chatColor = ChatColor.valueOf(getConfig().getString(ConfigKeys.NON_MYSQL_CHAT_COLOR_VALUE, String.valueOf(ChatColor.GRAY)));
            getConfig().set(ConfigKeys.NON_MYSQL_CHAT_COLOR_VALUE, this.chatColor.name());
        }

        this.forcePlayerNoPermissionErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_FORCE, this.forcePlayerNoPermissionErrorMessage);

        if(!this.mysqlEnabled || this.forcePlayerNoPermissionErrorMessage) {
            this.playerNoPermissionErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cYou don't have permission to perform this action!");
            getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_VALUE, this.playerNoPermissionErrorMessage);
        }

        this.forcePlayerNotFoundErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_FORCE, this.forcePlayerNotFoundErrorMessage);

        if(!this.mysqlEnabled || this.forcePlayerNotFoundErrorMessage) {
            this.playerNotFoundErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_VALUE, "§l§cERROR! The specified player could not be found!");
            getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_VALUE, this.playerNotFoundErrorMessage);
        }

        this.forceCommandOnlyPlayerErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_FORCE, this.forceCommandOnlyPlayerErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandOnlyPlayerErrorMessage) {
            this.commandOnlyPlayerErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cThis command can only be executed by a player!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_VALUE, this.commandOnlyPlayerErrorMessage);
        }

        this.forceCommandErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_FORCE, this.forceCommandErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandErrorMessage) {
            this.commandErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cError while executing command!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_VALUE, this.commandErrorMessage);
        }

        this.forceCommandInvalidArgsErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_FORCE, this.forceCommandInvalidArgsErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandInvalidArgsErrorMessage) {
            this.commandInvalidArgsErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cInvalid arguments!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_VALUE, this.commandInvalidArgsErrorMessage);
        }

        this.forceCommandInternalErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_FORCE, this.forceCommandInternalErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandInternalErrorMessage) {
            this.commandInternalErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cInternal error while executing command!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_VALUE, this.commandInternalErrorMessage);
        }

        this.forceUserTimeout = getConfig().getBoolean(ConfigKeys.NON_MYSQL_USER_TIMEOUT_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_USER_TIMEOUT_FORCE, this.forceUserTimeout);

        if(!this.mysqlEnabled || this.forceUserTimeout) {
            this.userTimeout = getConfig().getLong(ConfigKeys.NON_MYSQL_USER_TIMEOUT_VALUE, 5);
            getConfig().set(ConfigKeys.NON_MYSQL_USER_TIMEOUT_VALUE, this.userTimeout);
        }

        getLogger().log(Level.INFO, "config successfully setup!");
        getLogger().log(Level.INFO, "attempting to save config...");
        saveConfig();
        saveDefaultConfig();
        getLogger().log(Level.INFO, "config successfully saved!");
    }

    @Override
    public void onEnable() {
        try {
            getLogger().log(Level.INFO, "attempting to enable pluginapi...");
            RPlugin.instance = this;

            final PluginInfo info = getClass().getDeclaredAnnotation(PluginInfo.class);
            Class<? extends RUser> userClass = RUser.class;
            Class<? extends RUserManager> userManagerClass = RUserManager.class;
            Class<? extends ServerMaintenanceManager> maintenanceManagerClass = ServerMaintenanceManager.class;
            Class<? extends RConfig>[] localisationConfigs = new Class[0];

            if(info != null) {
                userClass = info.userClass();
                userManagerClass = info.userManagerClass();
                localisationConfigs = info.localisationConfigClasses();
            }

            setupConfig();

            this.listenerManager = new ListenerManager();
            this.commandManager = new CommandManager();
            this.userManager = userManagerClass.getDeclaredConstructor(Class.class, long.class).newInstance(userClass, this.userTimeout);
            this.serverMaintenanceManager = new ServerMaintenanceManager(this.maintenanceEnabled, this.maintenanceMessage);
            this.itemStackManager = new ItemStackManager();
            this.scoreboardManager = new ScoreboardManager();
            this.hologramManager = new HologramManager();
            this.userInputWindowManager = new UserInputWindowManager();
            this.hexCodeManager = new HexCodeManager();
            this.cloudNetManager = new CloudNetManager();
            this.localisationManager = new LocalisationManager(localisationConfigs);
            this.configManager = new ConfigManager();
            this.listenerManager.registerAll("me.xra1ny.pluginapi.listeners");
            getLogger().log(Level.INFO, "pluginapi enabled successfully!");

            try {
                getLogger().log(Level.INFO, "attempting to enable external plugin...");
                onPluginEnable();
                saveConfig();
                saveDefaultConfig();
                this.startupTime = System.currentTimeMillis();

                for(RConfig config : this.configManager.getConfigs()) {
                    config.update();
                }

                getLogger().log(Level.INFO, "external plugin enabled successfully!");
            }catch(Exception ex) {
                getLogger().log(Level.SEVERE, "error while enabling external plugin!", ex);
            }
        }catch(Exception ex) {
            getLogger().log(Level.SEVERE, "error while enabling pluginapi!", ex);
        }
    }

    @Override
    public void onDisable() {
        try {
            getLogger().log(Level.INFO, "attempting to disable pluginapi...");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer("Server is restarting...");
            }

            for(Hologram hologram : hologramManager.getHolograms()) {
                hologram.remove();
            }

            getLogger().log(Level.INFO, "pluginapi disabled successfully!");

            try {
                getLogger().log(Level.INFO, "attempting to disable external plugin...");
                onPluginDisable();
                saveConfig();
                getLogger().log(Level.INFO, "external plugin disabled successfully!");
            }catch(Exception ex) {
                getLogger().log(Level.SEVERE, "error while disabling external plugin!", ex);
            }
        }catch(Exception ex) {
            getLogger().log(Level.SEVERE, "error while disabling pluginapi!", ex);
        }
    }

    public static int broadcastMessage(@NotNull String... message) {
        final StringBuilder messageBuilder = new StringBuilder();

        for(String msg : message) {
            messageBuilder.append(RPlugin.getInstance().getChatColor()).append(msg);
        }

        return Bukkit.broadcastMessage(RPlugin.getInstance().getPrefix() + messageBuilder);
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String... message) {
        final StringBuilder messageBuilder = new StringBuilder();

        for(String msg : message) {
            messageBuilder.append(RPlugin.getInstance().getChatColor()).append(msg);
        }

        sender.sendMessage(RPlugin.getInstance().getPrefix() + messageBuilder);
    }

    public <T extends RUserManager> T getUserManager() {
        return (T) this.userManager;
    }
}