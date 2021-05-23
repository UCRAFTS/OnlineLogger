package net.ucrafts.onlinelogger.datasources;

import com.zaxxer.hikari.HikariDataSource;
import net.ucrafts.onlinelogger.Config;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDataSource implements DataSourceInterface
{

    protected Config config;
    protected String driverClassName;
    protected HikariDataSource dataSource;

    public AbstractDataSource(Config config)
    {
        this.config = config;
    }

    public void close()
    {
        this.dataSource.close();
    }

    public Connection getConnection() throws SQLException
    {
        return this.dataSource.getConnection();
    }
}
