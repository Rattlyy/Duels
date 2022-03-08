package it.rattly.duels.listeners;

import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import org.bukkit.event.Listener;

public record ListenerRegistrator(Duels plugin) implements Toggleable {

    @Override
    public void enable() throws IllegalStateException {
        plugin.getLogger().info("Adding and registering listeners...");

        int listeners = registerListeners(

        );

        plugin.getLogger().info(() -> listeners + " listeners registered!");
    }

    private int registerListeners(Listener... listeners) {
        return listeners.length;
    }
}
