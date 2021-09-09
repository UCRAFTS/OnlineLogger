package net.ucrafts.onlinelogger.tasks;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.ucrafts.onlinelogger.Config;
import net.ucrafts.onlinelogger.managers.OnlineManager;
import net.ucrafts.onlinelogger.types.ConfigType;

public class SaveOnlineTask implements Runnable {

    private final OnlineManager manager;
    private final ProxyServer server;
    private final Config config;

    public SaveOnlineTask(Config config, OnlineManager manager, ProxyServer server) {
        this.config = config;
        this.manager = manager;
        this.server = server;
    }

    @Override
    public void run() {
        for (RegisteredServer server : this.server.getAllServers()) {
            String serverName = server.getServerInfo().getName();
            String proxyName = this.config.getConfig().getString(ConfigType.PROXY_NAME.getName());
            int online = server.getPlayersConnected().size();

            this.manager.save(proxyName, serverName, online);
        }
    }
}
