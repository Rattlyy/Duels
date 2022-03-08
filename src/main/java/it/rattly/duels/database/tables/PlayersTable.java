package it.rattly.duels.database.tables;

import it.rattly.duels.Duels;
import it.rattly.duels.database.DatabaseProvider;
import it.rattly.duels.database.DatabaseTable;
import it.rattly.duels.player.DuelsPlayer;
import it.rattly.duels.player.UserData;
import it.rattly.duels.utils.UUIDUtils;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class PlayersTable extends DatabaseTable {

    @Language("SQL")
    private static final String TABLE = """
            CREATE TABLE players (
                 id        INT auto_increment PRIMARY KEY,
                 
                 uuid      INT           NOT NULL,
                 name      VARCHAR(32)   NOT NULL,
                 
                 wins      INT DEFAULT 0 NOT NULL,
                 loss      INT DEFAULT 0 NOT NULL,
                 kills     INT DEFAULT 0 NOT NULL,
                 deaths    INT DEFAULT 0 NOT NULL,
                 winstreak INT DEFAULT 0 NOT NULL,
                 
                 CONSTRAINT duels_uuid_uindex UNIQUE (uuid)
            );
            """;

    private static final @Language("SQL") String SELECT =
            "SELECT * from players WHERE uuid = ?;";

    private static final @Language("SQL") String INSERT =
            "INSERT INTO players(uuid, name) VALUES(?, ?);";

    private static final @Language("SQL") String UPDATE =
            "UPDATE players SET wins = ?, loss = ?, kills = ?, deaths = ?, winstreak = ? WHERE uuid = ?";

    public PlayersTable(Duels duels, DatabaseProvider provider) {
        super(duels, provider, TABLE);
    }

    public UserData selectUser(UUID uuid, String name) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT)) {
            statement.setBytes(1, UUIDUtils.toBytes(uuid));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) return new UserData(duels, uuid, name, resultSet);
            else {
                int id = createUser(uuid, name);
                return new UserData(duels, uuid, name, id);
            }
        } catch (SQLException e) {
            duels.logError(e, "Unable to load or create player " + uuid + " - " + name);
            return null;
        }
    }

    public boolean update(UserData data) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            setFields(statement, data);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            duels.logError(e, "Unable to update player " + data.getUuid() + " - " + data.getName());
            return false;
        }
    }

    public void update(Collection<DuelsPlayer> players) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            for (DuelsPlayer player : players) {
                setFields(statement, player.getData());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private void setFields(PreparedStatement statement, UserData data) throws SQLException {
        statement.setInt(1, data.getWins());
        statement.setInt(2, data.getKills());
        statement.setInt(3, data.getDeaths());
        statement.setInt(4, data.getWinstreak());
        statement.setBytes(5, UUIDUtils.toBytes(data.getUuid()));
    }


    public int createUser(UUID uuid, String name) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setBytes(1, UUIDUtils.toBytes(uuid));
            statement.setString(2, name);

            //language=sql
            statement.addBatch("SELECT LAST_INSERT_ID()");

            ResultSet set = statement.executeQuery();
            return set.getInt("id");
        }
    }
}
