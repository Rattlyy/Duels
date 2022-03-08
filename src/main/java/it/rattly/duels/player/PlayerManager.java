package it.rattly.duels.player;

import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import it.rattly.duels.database.tables.PlayersTable;
import it.rattly.duels.listeners.impl.AccessListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerManager implements Toggleable {

    private final Duels duels;
    private final PlayersTable table;
    private final Map<UUID, UserData> users;
    private final Map<UUID, DuelsPlayer> players;
    private final Set<UUID> saving = Collections.synchronizedSet(new HashSet<>());

    public PlayerManager(Duels duels) {
        this.duels = duels;
        this.table = duels.getDatabaseProvider().getPlayersTable();
        this.users = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
    }

    @Override
    public void enable() throws IllegalStateException {
        duels.getLogger().info("Loading online players...");

        Bukkit.getOnlinePlayers().forEach(player -> {
            LoadStatus status = loadUser(player.getUniqueId(), player.getName());
            if (status == LoadStatus.LOADED) {
                status = loadPlayer(player);
                if (status == LoadStatus.LOADED) return;
            }

            player.kick(AccessListener.getLoadedErrorMessage(player.getUniqueId(), player.getName(), status));

            duels.getLogger().info(() -> "Loaded " + players.size() + " players!");
        });
    }

    @Override
    public void disable() throws SQLException {
        saveBatch();
        users.clear();
        players.clear();
    }

    public LoadStatus loadUser(UUID uuid, String name) {
        if (saving.contains(uuid)) {
            duels.getLogger().warning(() -> "Trying to load user " + uuid + " | " + name + " while he is still saving!");
            return LoadStatus.STILL_SAVING;
        }

        if (users.containsKey(uuid)) {
            duels.getLogger().warning(() -> "Trying to load user " + uuid + " | " + name + " even if it was already loaded!");
            return LoadStatus.ALREADY_LOADED;
        }

        UserData userData = table.selectUser(uuid, name);
        if (userData == null) return LoadStatus.ERROR_LOAD_USER;

        users.put(uuid, userData);
        return LoadStatus.LOADED;
    }

    public void unloadUser(UUID uuid) {
        users.remove(uuid);
    }

    public void quit(Player player) {
        String name = player.getName();

        if (users.remove(player.getUniqueId()) != null) {
            duels.getLogger().severe(() -> name + " had its user infos not unloaded after joining!");
        }

        DuelsPlayer duelsPlayer = players.remove(player.getUniqueId());

        if (duelsPlayer == null) {
            duels.getLogger().severe(() -> "Tried to save " + name + " which is not loaded!");
            return;
        }

        save(duelsPlayer);
    }

    public CompletableFuture<Void> save(DuelsPlayer duelsPlayer) {
        saving.add(duelsPlayer.getData().getUuid());
        return duels.getDatabaseProvider().supplyAsync(() -> table.update(duelsPlayer.getData())).thenAccept(deleted -> saving.remove(duelsPlayer.getData().getUuid()));
    }

    public void saveBatch() throws SQLException {
        Collection<DuelsPlayer> playersToSave = players.values();
        for (DuelsPlayer player : playersToSave) {
            saving.add(player.getData().getUuid());
        }

        table.update(playersToSave);

        for (DuelsPlayer player : playersToSave) {
            saving.remove(player.getData().getUuid());
        }
    }

    public DuelsPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @Nullable
    public DuelsPlayer getPlayer(UUID player) {
        return players.get(player);
    }

    public Collection<DuelsPlayer> getPlayers() {
        return players.values();
    }

    public Map<UUID, UserData> getUsers() {
        return users;
    }

    public Map<UUID, DuelsPlayer> getLoadedPlayers() {
        return players;
    }

    public LoadStatus loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        UserData data = users.remove(uuid);

        if (data == null) {
            duels.getLogger().warning(() -> uuid + " - " + player.getName() + " is logging even if his data was not loaded previously!");
            return LoadStatus.NOT_LOADED;
        }

        if (players.containsKey(uuid)) {
            duels.getLogger().warning(() -> uuid + " - " + player.getName() + " is logging even if he is already loaded!");
            return LoadStatus.ALREADY_LOADED_PLAYER;
        }

        players.put(uuid, new DuelsPlayer(data, player, duels));
        return LoadStatus.LOADED;
    }

    public enum LoadStatus {
        ALREADY_LOADED(0),
        STILL_SAVING(1),
        ERROR_LOAD_USER(2),
        PRE_LOGIN_REALLOWED(3),
        NOT_LOADED(4),
        ALREADY_LOADED_PLAYER(5),
        LOADED(6),
        LOGIN_REALLOWED(7),
        RE_LOAD(8);

        private final int errorCode;

        LoadStatus(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }
}
