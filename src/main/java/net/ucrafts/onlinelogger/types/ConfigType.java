package net.ucrafts.onlinelogger.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfigType {

    DB_PREFIX("db.tablesPrefix"),
    REDIS_SERVERS_ONLINE("redis.serversOnline"),
    UPDATE_PERIOD("settings.period"),
    PROXY_NAME("settings.proxyName");

    private final String name;

}
