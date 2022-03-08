package it.rattly.duels.plugin;

import it.rattly.duels.Duels;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DuelsPlugin extends JavaPlugin {

    private Duels duels;

    @Override
    public void onLoad() {
        try {
            duels = new Duels(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e, () -> "An error occurred while loading Duels");
        }
    }

    @Override
    public void onEnable() {
        try {
            duels.enable();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e, () -> "An error occurred while enabling Duels");
        }
    }

    @Override
    public void onDisable() {
        try {
            duels.disable();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e, () -> "An error occurred while disabling Duels");
        }
    }
}
