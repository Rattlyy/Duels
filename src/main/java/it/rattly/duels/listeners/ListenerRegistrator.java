package it.rattly.duels.listeners;

import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import it.rattly.duels.listeners.impl.AccessListener;
import org.bukkit.event.Listener;

public record ListenerRegistrator(Duels plugin) implements Toggleable {

    @Override
    public void enable() throws IllegalStateException {
        plugin.getLogger().info("Registering listeners...");

        int listeners = registerListeners(
            new AccessListener(plugin)
        );

        plugin.getLogger().info(() -> listeners + " listeners registered!");
    }

    private int registerListeners(Listener... listeners) {
        return listeners.length;
    }
}
