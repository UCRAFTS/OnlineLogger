package net.ucrafts.onlinelogger.datasources;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ucrafts.onlinelogger.Config;
import net.ucrafts.onlinelogger.types.ConfigType;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySQLDataSource extends AbstractDataSource implements DataSourceInterface {

    private final Logger logger;
    protected String driverClassName = "com.mysql.cj.jdbc.Driver";

    @Inject
    public MySQLDataSource(Config config, Logger logger) {
        super(config);

        this.logger = logger;

        if (this.dataSource != null) {
            this.close();
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(this.driverClassName);
        hikariConfig.setUsername(this.config.getConfig().getString(ConfigType.DB_USER.getName()));
        hikariConfig.setPassword(this.config.getConfig().getString(ConfigType.DB_PASS.getName()));
        hikariConfig.setMaximumPoolSize(this.config.getConfig().getInt(ConfigType.DB_POOL_SIZE.getName()));
        hikariConfig.setPoolName("OnlineLoggerPool");
        hikariConfig.setJdbcUrl(
                String.format(
                        "jdbc:mysql://%s:%s/%s?useSSL=false&verifyServerCertificate=false&allowPublicKeyRetrieval=true&autoReconnect=true&serverTimezone=UTC",
                        this.config.getConfig().getString(ConfigType.DB_HOST.getName()),
                        this.config.getConfig().getInt(ConfigType.DB_PORT.getName()),
                        this.config.getConfig().getString(ConfigType.DB_BASE.getName())
                )
        );

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("logWriter", new PrintWriter(System.out));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void createTables() {
        this.createServerOnlineTable();
    }

    private void createServerOnlineTable() {
        String query = "create table if not exists %s_servers (id int not null auto_increment primary key, proxy varchar(255) not null, server varchar(255) not null, online int not null, created_at datetime not null);";

        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query.replace("%s", this.config.getConfig().getString(ConfigType.DB_PREFIX.getName())));
            preparedStatement.execute();
        } catch (Exception e) {
            this.logger.error("Cant create server online table: " + e.getMessage());
        }
    }
}
