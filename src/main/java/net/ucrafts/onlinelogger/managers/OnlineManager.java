package net.ucrafts.onlinelogger.managers;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import net.ucrafts.onlinelogger.Config;
import net.ucrafts.onlinelogger.types.ConfigType;
import net.ucrafts.server.pools.jdbc.JdbcHandler;
import net.ucrafts.server.pools.redis.RedisHandler;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class OnlineManager {

    private final JdbcHandler jdbcHandler;
    private final RedisHandler redisHandler;
    private final Config config;
    private final Logger logger;

    public void save(String proxyName, String serverName, int serverOnline) {
        final String query = String.format(
                "insert into %s_servers values(null, ?, ?, ?, now())",
                this.config.getConfig().getString(ConfigType.DB_PREFIX.getName())
        );

        try (Connection connection = this.jdbcHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, proxyName);
            statement.setString(2, serverName);
            statement.setInt(3, serverOnline);
            statement.executeUpdate();

            try (StatefulRedisConnection<String, String> redis = this.redisHandler.fetchConnection()) {
                RedisCommands<String, String> commands = redis.sync();
                commands.select(this.config.getConfig().getInt(ConfigType.REDIS_SERVERS_ONLINE.toString()));
                commands.hset(proxyName, serverName, String.valueOf(serverOnline));
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    public int getAllOnline() {
        try (StatefulRedisConnection<String, String> redis = this.redisHandler.fetchConnection()) {
            RedisCommands<String, String> commands = redis.sync();
            commands.select(this.config.getConfig().getInt(ConfigType.REDIS_SERVERS_ONLINE.toString()));

            List<String> proxyList = commands.keys("*");
            return proxyList.stream().map(commands::hgetall)
                    .flatMap(proxyOnline -> proxyOnline.entrySet().stream())
                    .mapToInt(entry -> Integer.parseInt(entry.getValue()))
                    .sum();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            return 0;
        }
    }

    public void createServerOnlineTable() {
        final String query = String.format(
                "create table if not exists %s_servers (id int not null auto_increment primary key, proxy varchar(255) not null, server varchar(255) not null, online int not null, created_at datetime not null);",
                this.config.getConfig().getString(ConfigType.DB_PREFIX.getName())
        );

        try (Connection connection = this.jdbcHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (Exception e) {
            this.logger.error("Cant create server online table: " + e.getMessage());
        }
    }

}
