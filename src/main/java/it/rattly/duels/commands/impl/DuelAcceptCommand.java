package it.rattly.duels.commands.impl;

import it.rattly.duels.Duels;
import it.rattly.duels.commands.AbstractCommand;
import it.rattly.duels.player.DuelsPlayer;
import it.rattly.duels.utils.Palette;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class DuelAcceptCommand extends AbstractCommand {
    public DuelAcceptCommand(Duels duels) {
        super(duels, "duelaccept");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    text().append(
                            text("You can't use this command from console.")
                    ).color(Palette.ERROR)
            );

            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(
                    text().append(
                            text("Usage: /duelaccept <name>")
                    ).color(Palette.ERROR)
            );

            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(
                    text().append(
                            text("The person you mentioned is offline or non-existant.")
                    ).color(Palette.ERROR)
            );

            return true;
        }

        DuelsPlayer duelsPlayer = duels.getPlayerManager().getPlayer(player);
        if (duelsPlayer.isInvitedBy(target.getUniqueId())) {
            duelsPlayer.acceptInvite(duelsPlayer.getInviteBy(target.getUniqueId()).get());
        } else {
            sender.sendMessage(
                    text().append(
                            text("You aren't invited by that player.")
                    ).color(Palette.ERROR)
            );

            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null; //TODO
    }
}
