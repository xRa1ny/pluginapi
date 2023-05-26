package me.xra1ny.pluginapi;

import lombok.Getter;
import me.xra1ny.pluginapi.listeners.DefaultPluginConnectionListener;
import me.xra1ny.pluginapi.listeners.DefaultPluginListener;
import me.xra1ny.pluginapi.models.cloudnet.CloudNetManager;
import me.xra1ny.pluginapi.models.color.HexCodeManager;
import me.xra1ny.pluginapi.models.command.CommandManager;
import me.xra1ny.pluginapi.models.item.ItemStackManager;
import me.xra1ny.pluginapi.models.listener.ListenerManager;
import me.xra1ny.pluginapi.models.maintenance.MaintenanceManager;
import me.xra1ny.pluginapi.models.scoreboard.ScoreboardManager;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.models.user.UserInputWindowManager;
import me.xra1ny.pluginapi.models.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
     * the config setting of the mysql url port to connect to
     */
    @Getter
    private int mysqlPort;

    /**
     * the config setting of the mysql servers username to login as
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlUsername;

    /**
     * the config setting of the mysql servers user password to login as
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlPassword;

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
    @Getter(onMethod = @__(@NotNull))
    private UserManager userManager;

    /**
     * the maintenance manager responsible for storing all maintenance related information and managing them
     */
    @Getter(onMethod = @__(@NotNull))
    private MaintenanceManager maintenanceManager;

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

        getLogger().setLevel(Level.parse(getConfig().getString("logging-level", "ALL")));
        getConfig().set("logging-level", getLogger().getLevel().toString());

        ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");

        if(mysql == null) {
            mysql = getConfig().createSection("mysql");
        }

        this.mysqlEnabled = mysql.getBoolean("enabled", false);
        mysql.set("enabled", this.mysqlEnabled);

        if(this.mysqlEnabled) {
            this.mysqlUrl = mysql.getString("url", "127.0.0.1");
            mysql.set("url", this.mysqlUrl);

            this.mysqlPort = mysql.getInt("port", 3306);
            mysql.set("port", this.mysqlPort);

            this.mysqlUsername = mysql.getString("username", "root");
            mysql.set("username", this.mysqlUsername);

            this.mysqlPassword = mysql.getString("password", "");
            mysql.set("password", this.mysqlPassword);
        }

        ConfigurationSection nonMysql = getConfig().getConfigurationSection("non-mysql");

        if(nonMysql == null) {
            nonMysql = getConfig().createSection("non-mysql");
        }

        this.forceNonMysqlSettings = nonMysql.getBoolean("force", true);
        nonMysql.set("force", this.forceNonMysqlSettings);

        if(!this.mysqlEnabled || this.forceNonMysqlSettings) {
            this.prefix = nonMysql.getString("prefix", ChatColor.BOLD + "MyAwesomePlugin  ");
            nonMysql.set("prefix", this.prefix);

            this.chatColor = ChatColor.valueOf(nonMysql.getString("chat-color", String.valueOf(ChatColor.GRAY)));
            nonMysql.set("chat-color", this.chatColor.name());

            this.playerNoPermissionErrorMessage = nonMysql.getString("player-no-permission-error-message", "§l§cFEHLER! §r§cDafür hast du keine Rechte!");
            nonMysql.set("player-no-permission-error-message", this.playerNoPermissionErrorMessage);

            this.onlyPlayerCommandErrorMessage = nonMysql.getString("only-player-command-error-message", "§l§cFEHLER! §r§cDieser Command kann nur durch einen Spieler ausgeführt werden!");
            nonMysql.set("only-player-command-error-message", this.onlyPlayerCommandErrorMessage);

            this.commandErrorMessage = nonMysql.getString("command-error-message", "§l§cFEHLER! §r§cCommand konnte nicht ausgeführt werden!");
            nonMysql.set("command-error-message", this.commandErrorMessage);

            this.commandInvalidArgsErrorMessage = nonMysql.getString("command-invalid-args-error-message", "§l§cFEHLER! §r§cUngültige Command Argumente!");
            nonMysql.set("command-invalid-args-error-message", this.commandInvalidArgsErrorMessage);

            this.commandInternalErrorMessage = nonMysql.getString("command-internal-error-message", "§l§cFEHLER! §r§cInterner Fehler beim Ausführen des Commands!");
            nonMysql.set("command-internal-error-message", this.commandInternalErrorMessage);

            this.userTimeout = nonMysql.getLong("user-timeout", 20);
            nonMysql.set("user-timeout", this.userTimeout);
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

            Class<? extends RUser> userClass = RUser.class;
            final PluginInfo pluginInfo = getClass().getDeclaredAnnotation(PluginInfo.class);

            if(pluginInfo != null) {
                userClass = pluginInfo.userClass();
            }

            setupConfig();

            this.listenerManager = new ListenerManager();
            this.commandManager = new CommandManager();
            this.userManager = new UserManager(userClass, this.userTimeout);
            this.maintenanceManager = new MaintenanceManager();
            this.itemStackManager = new ItemStackManager();
            this.scoreboardManager = new ScoreboardManager();
            this.userInputWindowManager = new UserInputWindowManager();
            this.hexCodeManager = new HexCodeManager();
            this.cloudNetManager = new CloudNetManager();

            // TODO: add database support...
            // if(this.mysqlEnabled) {
                // this.databaseApiManager = new DatabaseApiManager();
            // }

            this.listenerManager.register(new DefaultPluginConnectionListener());
            this.listenerManager.register(new DefaultPluginListener());

            getLogger().log(Level.INFO, "pluginapi successfully enabled!");

            try {
                getLogger().log(Level.INFO, "attempting to enable external plugin...");

                onPluginEnable();

                saveConfig();
                saveDefaultConfig();

                getLogger().log(Level.INFO, "external plugin successfully enabled!");
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

            getLogger().log(Level.INFO, "pluginapi successfully disabled!");

            try {
                getLogger().log(Level.INFO, "attempting to disable external plugin...");

                onPluginDisable();

                saveConfig();

                getLogger().log(Level.INFO, "external plugin successfully disabled!");
            }catch(Exception ex) {
                getLogger().log(Level.SEVERE, "error while disabling external plugin!", ex);
            }
        }catch(Exception ex) {
            getLogger().log(Level.SEVERE, "error while disabling pluginapi!", ex);
        }
    }
}
