package it.rattly.duels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import org.bukkit.configuration.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

public class DatabaseConnectionProvider implements Toggleable {


    private final Duels plugin;
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;

    private HikariDataSource dataSource;

    public DatabaseConnectionProvider(Duels plugin, Configuration config) {
        this.plugin = plugin;

        String key = "database.";
        this.host = config.getString(key + "host");
        this.port = config.getString(key + "port");
        this.database = config.getString(key + "database");
        this.user = config.getString(key + "user");
        this.password = config.getString(key + "password");
    }

    @Override
    public void enable() throws IllegalStateException {
        plugin.getLogger().info("Starting Hikari...");

        Properties properties = new Properties();

        properties.setProperty("driverClassName", "org.mariadb.jdbc.Driver");
        properties.setProperty("jdbcUrl", String.format("jdbc:mariadb://%s:%s/%s", host, port, database));
        properties.setProperty("dataSource.serverName", host);
        properties.setProperty("dataSource.user", user);
        properties.setProperty("dataSource.password", password);
        properties.setProperty("dataSource.databaseName", database);
        properties.setProperty("dataSource.portNumber", port);

        HikariConfig config = new HikariConfig(properties);

        config.setPoolName("DuelsPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("allowMultiQueries", true);
        config.setMaximumPoolSize(5);

        dataSource = new HikariDataSource(config);

        plugin.getLogger().info("Started Hikari!");
    }

    @Override
    public void disable() {
        dataSource.close();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}