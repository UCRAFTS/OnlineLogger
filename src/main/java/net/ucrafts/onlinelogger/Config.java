package net.ucrafts.onlinelogger;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FlatFile;
import net.ucrafts.onlinelogger.types.ConfigType;

import java.nio.file.Path;

public class Config {

    private final FlatFile config;

    @Inject
    public Config(@DataDirectory Path dataDirectory) {
        this.config = new Json("config", dataDirectory.toString());
        this.config.setDefault(ConfigType.DB_HOST.getName(), "127.0.0.1");
        this.config.setDefault(ConfigType.DB_PORT.getName(), 3306);
        this.config.setDefault(ConfigType.DB_BASE.getName(), "servers");
        this.config.setDefault(ConfigType.DB_USER.getName(), "user");
        this.config.setDefault(ConfigType.DB_PASS.getName(), "secret");
        this.config.setDefault(ConfigType.DB_POOL_SIZE.getName(), 5);
        this.config.setDefault(ConfigType.DB_PREFIX.getName(), "online");
        this.config.setDefault(ConfigType.REDIS_HOST.toString(), "127.0.0.1");
        this.config.setDefault(ConfigType.REDIS_PORT.toString(), 6379);
        this.config.setDefault(ConfigType.REDIS_PASS.toString(), "secret");
        this.config.setDefault(ConfigType.REDIS_TIMEOUT.toString(), 60);
        this.config.setDefault(ConfigType.REDIS_SERVERS_ONLINE.toString(), 7);
        this.config.setDefault(ConfigType.REDIS_POOL_SIZE.toString(), 5);
        this.config.setDefault(ConfigType.PROXY_NAME.getName(), "proxy");
        this.config.setDefault(ConfigType.UPDATE_PERIOD.getName(), 1);
    }

    public FlatFile getConfig() {
        return this.config;
    }
}
