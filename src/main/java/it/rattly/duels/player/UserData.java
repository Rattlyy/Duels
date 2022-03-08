package it.rattly.duels.player;

import it.rattly.duels.Duels;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
public final class UserData {

    private final Duels duels;
    private final UUID uuid;
    private final String name;
    private final int databaseId;

    private int wins;
    private int loss;
    private int kills;
    private int deaths;
    private int winstreak;

    public UserData(Duels duels, UUID uuid, String name, ResultSet resultSet) throws SQLException {
        this.duels = duels;
        this.uuid = uuid;
        this.name = name;

        this.databaseId = resultSet.getInt("id");
        this.wins = resultSet.getInt("wins");
        this.loss = resultSet.getInt("loss");
        this.kills = resultSet.getInt("kills");
        this.deaths = resultSet.getInt("deaths");
        this.winstreak = resultSet.getInt("winstreak");
    }

    public UserData(Duels duels, UUID uuid, String name, int id) {
        this.duels = duels;
        this.uuid = uuid;
        this.name = name;
        this.databaseId = id;

        this.wins = 0;
        this.loss = 0;
        this.kills = 0;
        this.deaths = 0;
        this.winstreak = 0;
    }
}
