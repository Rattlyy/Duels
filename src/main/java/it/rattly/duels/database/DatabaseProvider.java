package it.rattly.duels.database;

import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import it.rattly.duels.database.tables.PlayersTable;
import lombok.Getter;
import org.bukkit.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Getter
public class DatabaseProvider implements Toggleable {

    private final Duels duels;
    private final DatabaseConnectionProvider connectionProvider;

    //Tables
    private final PlayersTable playersTable;

    public DatabaseProvider(Duels duels, Configuration config) {
        this.duels = duels;
        this.connectionProvider = new DatabaseConnectionProvider(duels, config);

        this.playersTable = new PlayersTable(duels, this);
    }

    @Override
    public void enable() throws Exception {
        duels.getLogger().info("Starting the database..");

        connectionProvider.enable();
        createTables();

        duels.getLogger().info("Database started!");
    }

    @Override
    public void disable() {
        connectionProvider.disable();
    }

    public void createTables() throws SQLException {
        duels.getLogger().info("Creating tables..");

        int tables = createTables(
                playersTable
        );

        duels.getLogger().info(() -> "Created " + tables + " tables!");
    }

    public int createTables(DatabaseTable... tables) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            for (DatabaseTable table : tables) {
                for (String tableScript : table.getTableQuerys()) statement.addBatch(tableScript);
            }

            statement.executeBatch();
        }

        return tables.length;
    }

    public DatabaseConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public DataSource getDataSource() {
        return connectionProvider.getDataSource();
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, duels.getScheduler().getDatabaseExecutor());
    }

    public <T> CompletableFuture<T> callAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception t) {
                future.completeExceptionally(t);
            }
        }, duels.getScheduler().getDatabaseExecutor());
        return future;
    }

    public CompletableFuture<Runnable> runAsync(Runnable runnable) {
        CompletableFuture<Runnable> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                future.complete(runnable);
            } catch (Exception t) {
                future.completeExceptionally(t);
            }
        }, duels.getScheduler().getDatabaseExecutor());
        return future;
    }

    public void async(Runnable supplier) {
        CompletableFuture.runAsync(supplier, duels.getScheduler().getDatabaseExecutor());
    }
}
