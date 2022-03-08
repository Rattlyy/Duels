package it.rattly.duels;

import it.rattly.duels.database.DatabaseProvider;
import it.rattly.duels.player.PlayerManager;
import it.rattly.duels.plugin.DuelsPlugin;
import it.rattly.duels.utils.Scheduler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Duels implements Toggleable {

    // Plugin
    private final JavaPlugin plugin;
    private final Scheduler scheduler;

    // Toggleables
    private final DatabaseProvider databaseProvider;
    private final PlayerManager playerManager;

    // Latch
    private final CountDownLatch enableLatch = new CountDownLatch(1);

    public Duels(DuelsPlugin plugin) throws IllegalStateException {
        this.plugin = plugin;
        this.scheduler = new Scheduler(plugin);

        this.databaseProvider = new DatabaseProvider(this, plugin.getConfig());
        this.playerManager = new PlayerManager(this);
    }

    @Override
    public void enable() throws Exception {
        databaseProvider.enable();
        playerManager.enable();

        enableLatch.countDown();
    }

    @Override
    public void disable() throws Exception {
        playerManager.disable();
        databaseProvider.disable();
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public void logError(Exception e, String s) {
        getLogger().log(Level.SEVERE, e, () -> s);
    }
}