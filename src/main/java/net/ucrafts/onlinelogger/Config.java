package net.ucrafts.onlinelogger;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FlatFile;
import lombok.Getter;
import net.ucrafts.onlinelogger.types.ConfigType;

import java.nio.file.Path;

@Getter
public class Config {

    private final FlatFile config;

    @Inject
    public Config(@DataDirectory Path dataDirectory) {
        this.config = new Json("config", dataDirectory.toString());
        this.config.setDefault(ConfigType.DB_PREFIX.getName(), "online");
        this.config.setDefault(ConfigType.REDIS_SERVERS_ONLINE.toString(), 7);
        this.config.setDefault(ConfigType.PROXY_NAME.getName(), "proxy");
        this.config.setDefault(ConfigType.UPDATE_PERIOD.getName(), 1);
    }

}
