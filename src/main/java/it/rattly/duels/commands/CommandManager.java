package it.rattly.duels.commands;

import it.rattly.duels.Duels;
import it.rattly.duels.Toggleable;
import it.rattly.duels.commands.impl.DuelAcceptCommand;
import it.rattly.duels.commands.impl.DuelCommand;
import it.rattly.duels.commands.impl.DuelDenyCommand;

public record CommandManager(Duels duels) implements Toggleable {
    @Override
    public void enable() {
        duels.getLogger().info("Registering commands...");

        int commands = registerCommands(
                new DuelCommand(duels),
                new DuelAcceptCommand(duels),
                new DuelDenyCommand(duels)
        );

        duels.getLogger().info(() -> commands + " listeners registered!");
    }

    private int registerCommands(AbstractCommand... commands) {
        return commands.length;
    }
}
