package it.rattly.duels.utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import static net.kyori.adventure.text.Component.text;

public class Palette {
    private Palette() {
    }

    public static final TextColor PRIMARY_TEXT = NamedTextColor.GRAY;
    public static final TextColor PRIMARY_SEPARATOR = NamedTextColor.DARK_GRAY;

    public static final TextColor SUCCESS = NamedTextColor.GREEN;
    public static final TextColor WARN = NamedTextColor.YELLOW;
    public static final TextColor ERROR = NamedTextColor.RED;

    public static final TextComponent BLOCK = text("■").color(Palette.PRIMARY_SEPARATOR);
    public static final TextComponent ARROW = text("»").color(Palette.PRIMARY_SEPARATOR);
}
