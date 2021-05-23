package net.ucrafts.onlinelogger.managers;

import de.leonhard.storage.shaded.json.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;
import net.ucrafts.onlinelogger.Config;
import net.ucrafts.onlinelogger.datasources.AbstractDataSource;
import net.ucrafts.onlinelogger.types.ConfigType;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Time;
import java.util.Map;
import java.util.Set;

public class OnlineManager
{

    private Config config;
    private final AbstractDataSource dataSource;
    private final JedisPool jedis;
    private final Logger logger;

    public OnlineManager(Config config, AbstractDataSource dataSource, JedisPool jedis, Logger logger)
    {
        this.config = config;
        this.dataSource = dataSource;
        this.jedis = jedis;
        this.logger = logger;
    }

    public void save(String proxyName, String serverName, int serverOnline)
    {
        try (Connection connection = this.dataSource.getConnection()) {
            String query = "INSERT INTO %s_servers VALUES(NULL, ?, ?, ?, NOW())";
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query.replace("%s", this.config.getConfig().getString(ConfigType.DB_PREFIX.getName()))
            );

            preparedStatement.setString(1, proxyName);
            preparedStatement.setString(2, serverName);
            preparedStatement.setInt(3, serverOnline);
            preparedStatement.execute();

            Jedis j = this.jedis.getResource();
            j.select(this.config.getConfig().getInt(ConfigType.REDIS_SERVERS_ONLINE.toString()));
            j.hset(proxyName, serverName, String.valueOf(serverOnline));
            j.close();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    public int getAllOnline()
    {
        int online = 0;

        try {
            Jedis j = this.jedis.getResource();
            j.select(this.config.getConfig().getInt(ConfigType.REDIS_SERVERS_ONLINE.toString()));
            Set<String> proxyList = j.keys("*");

            for (String proxy : proxyList) {
                Map<String, String> proxyOnline = j.hgetAll(proxy);

                for (Map.Entry<String, String> entry : proxyOnline.entrySet()) {
                    online += Integer.parseInt(entry.getValue());
                }
            }

            j.close();

            return online;
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }

        return online;
    }
}
