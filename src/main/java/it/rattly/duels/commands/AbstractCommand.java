package it.rattly.duels.commands;

import it.rattly.duels.Duels;
import it.rattly.duels.utils.Palette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    protected Duels duels;

    protected AbstractCommand(Duels duels, String name) {
        Objects.requireNonNull(duels.getPlugin().getCommand(name)).setExecutor(this);
        Objects.requireNonNull(duels.getPlugin().getCommand(name)).setTabCompleter(this);
    }

    public static Component generateList(String title, Component... texts) {
        TextComponent.Builder component = text();

        component.append(
                newline(),
                text(title).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD),
                newline(), newline()
        );

        for (Component text : texts) {
            component.append(
                    text(" "),
                    Palette.BLOCK,
                    text(" "),
                    text,
                    newline()
            );
        }

        return component.build();
    }
}
