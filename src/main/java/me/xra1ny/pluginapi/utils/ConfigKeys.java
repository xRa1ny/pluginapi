package me.xra1ny.pluginapi.utils;

public interface ConfigKeys {
    String LOGGING_LEVEL = "logging-level";

    String MYSQL = "mysql";
    String MYSQL_ENABLED = "enabled";
    String MYSQL_URL = "url";
    String MYSQL_PORT = "port";
    String MYSQL_USERNAME = "username";
    String MYSQL_PASSWORD = "password";

    String NON_MYSQL = "non-mysql";
    String NON_MYSQL_FORCE = "force";
    String NON_MYSQL_PREFIX = "prefix";
    String NON_MYSQL_CHAT_COLOR = "chat-color";
    String NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE = "player-no-permission-error-message";
    String NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE = "command-only-player-error-message";
    String NON_MYSQL_COMMAND_ERROR_MESSAGE = "command-error-message";
    String NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE = "command-invalid-args-error-message";
    String NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE = "command-internal-error-message";
    String NON_MYSQL_USER_TIMEOUT = "user-timeout";
}