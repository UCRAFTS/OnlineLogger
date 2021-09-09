package net.ucrafts.onlinelogger.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.ucrafts.onlinelogger.managers.OnlineManager;

public class ProxyPingListener {

    private final OnlineManager manager;

    public ProxyPingListener(OnlineManager manager) {
        this.manager = manager;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent e) {
        final ServerPing.Builder pong = e.getPing().asBuilder();
        pong.onlinePlayers(this.manager.getAllOnline());

        e.setPing(pong.build());
    }
}
