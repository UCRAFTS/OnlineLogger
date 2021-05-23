package net.ucrafts.onlinelogger.types;

import org.jetbrains.annotations.NotNull;

public enum ConfigType
{

    DB_HOST("db.host"),
    DB_PORT("db.port"),
    DB_USER("db.user"),
    DB_PASS("db.pass"),
    DB_POOL_SIZE("db.poolSize"),
    DB_BASE("db.base"),
    DB_PREFIX("db.tablesPrefix"),
    REDIS_HOST("redis.host"),
    REDIS_PORT("redis.port"),
    REDIS_TIMEOUT("redis.timeout"),
    REDIS_PASS("redis.pass"),
    REDIS_SERVERS_ONLINE("redis.serversOnline"),
    REDIS_POOL_SIZE("redis.poolSize"),
    UPDATE_PERIOD("settings.period"),
    PROXY_NAME("settings.proxyName");

    private final String name;

    ConfigType(@NotNull final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
