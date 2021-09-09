package net.ucrafts.onlinelogger.datasources;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceInterface {

    void close();

    void createTables();

    Connection getConnection() throws SQLException;
}
