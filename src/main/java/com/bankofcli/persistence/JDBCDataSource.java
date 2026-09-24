package com.bankofcli.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JDBCDataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static Properties props = new Properties();

    static {
        try (InputStream in = JDBCDataSource.class.getClassLoader()
            .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        config.setJdbcUrl(props.getProperty("DB_URL"));
        config.setUsername(props.getProperty("DB_USER"));
        config.setPassword(props.getProperty("DB_PASSWORD"));
        ds = new HikariDataSource( config );
    }

    private JDBCDataSource() {}

    public static Connection getConnection() {
        try{
            return ds.getConnection();
        }
        catch(SQLException e){
            System.out.printf("Unable to get Database Connection: %s", e.getMessage());
        }
        return null;
    }
}
