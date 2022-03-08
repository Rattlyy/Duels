package it.rattly.duels.database;

import it.rattly.duels.Duels;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseTable {

    protected final Duels duels;
    protected final DatabaseProvider provider;
    private final String[] tableQuerys;

    protected DatabaseTable(Duels duels, DatabaseProvider provider, @Language("SQL") String... tableQuerys) {
        this.tableQuerys = tableQuerys;
        this.duels = duels;
        this.provider = provider;
    }

    public String[] getTableQuerys() {
        return tableQuerys;
    }

    protected Connection getConnection() throws SQLException {
        return provider.getConnection();
    }
}
