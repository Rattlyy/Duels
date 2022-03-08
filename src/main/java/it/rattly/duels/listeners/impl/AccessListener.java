package it.rattly.duels.listeners.impl;

import it.rattly.duels.Duels;
import it.rattly.duels.listeners.DuelsListener;
import it.rattly.duels.player.PlayerManager;
import it.rattly.duels.utils.Palette;
import it.rattly.duels.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class AccessListener extends DuelsListener {

    public AccessListener(Duels duels) {
        super(duels);
    }

    public static Component getLoadedErrorMessage(UUID uuid, String name, PlayerManager.LoadStatus status) {
        return text().append(
                        text("There was an error in your connection!").color(Palette.ERROR),
                        newline(),
                        newline(),
                        text("UUID: " + uuid).color(Palette.PRIMARY_TEXT),
                        newline(),
                        text("Name: " + name).color(Palette.PRIMARY_TEXT),
                        newline(),
                        text("Date: " + Utils.formatDate(new Date())).color(Palette.PRIMARY_TEXT),
                        newline(),
                        text("Error code: " + status.getErrorCode()).color(Palette.PRIMARY_TEXT))
                .build();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLowAsyncPlayerJoin(AsyncPlayerPreLoginEvent event) {
        try {
            duels.getEnableLatch().await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            duels.getLogger().info(() -> "Another plugin has cancelled the connection for " + uuid + " - " + name + ". No data will be loaded.");
            return;
        }

        PlayerManager.LoadStatus status = duels.getPlayerManager().loadUser(uuid, name);
        if (status != PlayerManager.LoadStatus.LOADED) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, getLoadedErrorMessage(uuid, name, status));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitorAsyncPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if (!duels.getPlayerManager().getUsers().containsKey(uuid) && event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            String name = event.getName();

            duels.getLogger().severe(() -> "Player connection was re-allowed but " + uuid + " - " + name + " is not loaded!");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, getLoadedErrorMessage(uuid, name, PlayerManager.LoadStatus.PRE_LOGIN_REALLOWED));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLowPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            duels.getLogger().info(() -> "Another plugin has cancelled the login for " + uuid + " - " + name + ". No player will be loaded.");
            duels.getPlayerManager().unloadUser(uuid);
            return;
        }

        PlayerManager.LoadStatus status = duels.getPlayerManager().loadPlayer(player);

        if (status != PlayerManager.LoadStatus.LOADED) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, getLoadedErrorMessage(player.getUniqueId(), player.getName(), status));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitorPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!duels.getPlayerManager().getLoadedPlayers().containsKey(uuid) && event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            String name = player.getName();

            duels.getLogger().severe(() -> "Player login was re-allowed for " + uuid + " - " + name);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, getLoadedErrorMessage(uuid, name, PlayerManager.LoadStatus.LOGIN_REALLOWED));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        duels.getPlayerManager().quit(event.getPlayer());
    }
}
