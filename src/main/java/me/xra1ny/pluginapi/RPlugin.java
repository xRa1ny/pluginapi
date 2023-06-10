package me.xra1ny.pluginapi;

import lombok.Getter;
import me.xra1ny.pluginapi.listeners.DefaultPluginConnectionListener;
import me.xra1ny.pluginapi.listeners.DefaultPluginListener;
import me.xra1ny.pluginapi.models.cloudnet.CloudNetManager;
import me.xra1ny.pluginapi.models.color.HexCodeManager;
import me.xra1ny.pluginapi.models.command.CommandManager;
import me.xra1ny.pluginapi.models.hologram.HologramManager;
import me.xra1ny.pluginapi.models.item.ItemStackManager;
import me.xra1ny.pluginapi.models.listener.ListenerManager;
import me.xra1ny.pluginapi.models.maintenance.ServerMaintenanceManager;
import me.xra1ny.pluginapi.models.scoreboard.ScoreboardManager;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.models.user.RUserManager;
import me.xra1ny.pluginapi.models.user.UserInputWindowManager;
import me.xra1ny.pluginapi.utils.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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

    /**
     * the config setting of the mysql url to connect to
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlUrl;

    /**
     * the config setting whether to force non mysql config settings even when a mysql connection has been established
     */
    @Getter
    private boolean forceNonMysqlSettings;

    /**
     * the global prefix of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private String prefix;

    /**
     * the global chat color of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private ChatColor chatColor;

    /**
     * the global message to display when an error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandErrorMessage;

    /**
     * the global message to display when an internal error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandInternalErrorMessage;

    /**
     * the global message to display when a command has been passed invalid arguments
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandInvalidArgsErrorMessage;

    /**
     * the global error message displayed when a player does not have the permissions to perform an action
     */
    @Getter(onMethod = @__(@NotNull))
    private String playerNoPermissionErrorMessage;

    @Getter(onMethod = @__(@NotNull))
    private String playerNotFoundErrorMessage;

    /**
     * the global error message displayed when the console performs a command that can only be executed by a player
     */
    @Getter(onMethod = @__(@NotNull))
    private String onlyPlayerCommandErrorMessage;

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

    /**
     * the global player identifier used in strings
     */
    public final String PLAYER_IDENTIFIER = "%PLAYER%";

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

        ConfigurationSection maintenance = RPlugin.getInstance().getConfig().getConfigurationSection(ConfigKeys.MAINTENANCE);

        if(maintenance == null) {
            maintenance = RPlugin.getInstance().getConfig().createSection(ConfigKeys.MAINTENANCE);
        }

        this.maintenanceEnabled = maintenance.getBoolean(ConfigKeys.MAINTENANCE_ENABLED, false);
        maintenance.set(ConfigKeys.MAINTENANCE_ENABLED, this.maintenanceEnabled);

        this.maintenanceMessage = maintenance.getString(ConfigKeys.MAINTENANCE_MESSAGE, "§lMaintenance!");
        maintenance.set(ConfigKeys.MAINTENANCE_MESSAGE, this.maintenanceMessage);

        this.mysqlEnabled = getConfig().getBoolean(ConfigKeys.MYSQL_ENABLED, false);
        getConfig().set(ConfigKeys.MYSQL_ENABLED, this.mysqlEnabled);

        ConfigurationSection nonMysql = getConfig().getConfigurationSection(ConfigKeys.NON_MYSQL);

        if(nonMysql == null) {
            nonMysql = getConfig().createSection(ConfigKeys.NON_MYSQL);
        }

        this.forceNonMysqlSettings = nonMysql.getBoolean(ConfigKeys.NON_MYSQL_FORCE, true);
        nonMysql.set(ConfigKeys.NON_MYSQL_FORCE, this.forceNonMysqlSettings);

        if(!this.mysqlEnabled || this.forceNonMysqlSettings) {
            this.prefix = nonMysql.getString(ConfigKeys.NON_MYSQL_PREFIX, "§lMyPlugin  ");
            nonMysql.set(ConfigKeys.NON_MYSQL_PREFIX, this.prefix);

            this.chatColor = ChatColor.valueOf(nonMysql.getString(ConfigKeys.NON_MYSQL_CHAT_COLOR, String.valueOf(ChatColor.GRAY)));
            nonMysql.set(ConfigKeys.NON_MYSQL_CHAT_COLOR, this.chatColor.name());

            this.playerNoPermissionErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE, "§l§cERROR! §r§cYou don't have permission to perform this action!");
            nonMysql.set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE, this.playerNoPermissionErrorMessage);

            this.playerNotFoundErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE, "§l§cERROR! The specified player could not be found!");
            nonMysql.set(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE, this.playerNotFoundErrorMessage);

            this.onlyPlayerCommandErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE, "§l§cERROR! §r§cThis command can only be executed by a player!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE, this.onlyPlayerCommandErrorMessage);

            this.commandErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE, "§l§cERROR! §r§cError while executing command!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE, this.commandErrorMessage);

            this.commandInvalidArgsErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE, "§l§cERROR! §r§cInvalid arguments!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE, this.commandInvalidArgsErrorMessage);

            this.commandInternalErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE, "§l§cERROR! §r§cInternal error while executing command!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE, this.commandInternalErrorMessage);

            this.userTimeout = nonMysql.getLong(ConfigKeys.NON_MYSQL_USER_TIMEOUT, 20);
            nonMysql.set(ConfigKeys.NON_MYSQL_USER_TIMEOUT, this.userTimeout);
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

            if(info != null) {
                userClass = info.userClass();
                userManagerClass = info.userManagerClass();
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

            this.listenerManager.register(new DefaultPluginConnectionListener());
            this.listenerManager.register(new DefaultPluginListener());

            getLogger().log(Level.INFO, "pluginapi enabled successfully!");

            try {
                getLogger().log(Level.INFO, "attempting to enable external plugin...");

                onPluginEnable();

                saveConfig();
                saveDefaultConfig();

                this.startupTime = System.currentTimeMillis();

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