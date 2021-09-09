package net.ucrafts.onlinelogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.ucrafts.onlinelogger.listeners.ProxyPingListener;
import net.ucrafts.onlinelogger.managers.OnlineManager;
import net.ucrafts.onlinelogger.tasks.SaveOnlineTask;
import net.ucrafts.onlinelogger.types.ConfigType;
import net.ucrafts.server.pools.PoolsPlugin;
import org.slf4j.Logger;

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
        },
        dependencies = {
                @Dependency(id = "upools-velocity")
        }
)
public class OnlineLoggerPlugin {

    private final ProxyServer server;
    private final PoolsPlugin poolsPlugin;
    private final OnlineManager manager;
    private final Logger logger;
    private final HashSet<ScheduledTask> tasks = new HashSet<>();
    private final Config config;

    @Inject
    public OnlineLoggerPlugin(
            ProxyServer server,
            Config config,
            Logger logger,
            @Named("upools-velocity") PluginContainer poolsPlugin
    ) {
        this.server = server;
        this.config = config;
        this.logger = logger;
        this.poolsPlugin = (PoolsPlugin) poolsPlugin.getInstance().get();
        this.manager = new OnlineManager(
                this.poolsPlugin.getJdbcHandler(),
                this.poolsPlugin.getRedisHandler(),
                this.config,
                this.logger
        );
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
        this.manager.createServerOnlineTable();
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
    }

}
