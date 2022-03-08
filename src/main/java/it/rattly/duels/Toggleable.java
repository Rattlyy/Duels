package it.rattly.duels;

public interface Toggleable {
    void enable() throws Exception;

    default void disable() throws Exception { }

    default void reload() { }
}
