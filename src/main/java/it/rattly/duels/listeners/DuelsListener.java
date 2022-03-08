package it.rattly.duels.listeners;

import it.rattly.duels.Duels;
import org.bukkit.event.Listener;

public abstract class DuelsListener implements Listener {

    protected final Duels duels;

    protected DuelsListener(Duels laroc) {
        this.duels = laroc;

        laroc.getPlugin().getServer().getPluginManager().registerEvents(this, laroc.getPlugin());
    }
}
