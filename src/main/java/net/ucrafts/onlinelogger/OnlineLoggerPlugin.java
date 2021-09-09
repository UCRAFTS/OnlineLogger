package net.ucrafts.onlinelogger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.ucrafts.onlinelogger.datasources.AbstractDataSource;
import net.ucrafts.onlinelogger.datasources.MySQLDataSource;
import net.ucrafts.onlinelogger.listeners.ProxyPingListener;
import net.ucrafts.onlinelogger.managers.OnlineManager;
import net.ucrafts.onlinelogger.tasks.SaveOnlineTask;
import net.ucrafts.onlinelogger.types.ConfigType;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "onlinelogger",
        name = "OnlineLogger",
        version = "1.0.0",
        url = "https://ucrafts.net",
        description = "Servers online logger",
        authors = {
                "Alexander Repin / oDD1"
        }
)
public class OnlineLoggerPlugin {

    private final ProxyServer server;
    private final AbstractDataSource dataSource;
    private final JedisPool jedis;
    private final OnlineManager manager;
    private final Logger logger;
    private final HashSet<ScheduledTask> tasks = new HashSet<>();
    private final Config config;

    @Inject
    public OnlineLoggerPlugin(ProxyServer server, Config config, Logger logger) {
        this.server = server;
        this.config = config;
        this.logger = logger;
        this.dataSource = new MySQLDataSource(this.config, this.logger);
        this.dataSource.createTables();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(this.config.getConfig().getInt(ConfigType.REDIS_POOL_SIZE.toString()));

        this.jedis = new JedisPool(
                poolConfig,
                this.config.getConfig().getString(ConfigType.REDIS_HOST.toString()),
                this.config.getConfig().getInt(ConfigType.REDIS_PORT.toString()),
                this.config.getConfig().getInt(ConfigType.REDIS_TIMEOUT.toString()),
                this.config.getConfig().getString(ConfigType.REDIS_PASS.toString())
        );

        this.manager = new OnlineManager(this.config, this.dataSource, this.jedis, this.logger);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent e) {
        this.server.getEventManager().register(this, new ProxyPingListener(this.manager));
        this.tasks.add(
                this.server.getScheduler()
                        .buildTask(this, new SaveOnlineTask(this.config, this.manager, this.server))
                        .repeat(this.config.getConfig().getInt(ConfigType.UPDATE_PERIOD.getName()), TimeUnit.MINUTES)
                        .schedule()
        );
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
        for (RegisteredServer server : this.server.getAllServers()) {
            String serverName = server.getServerInfo().getName();
            String proxyName = this.config.getConfig().getString(ConfigType.PROXY_NAME.getName());

            this.manager.save(proxyName, serverName, 0);
        }

        for (ScheduledTask task : this.tasks) {
            task.cancel();
        }

        this.dataSource.close();
        this.jedis.close();
    }
}
