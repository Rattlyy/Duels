package it.rattly.duels.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("UnusedReturnValue")
public record Scheduler(JavaPlugin plugin) {

    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(5);

    public Executor getSyncExecutor() {
        return this::sync;
    }

    public Executor getDatabaseExecutor() {
        return databaseExecutor;
    }

    public BukkitTask sync(BukkitRunnable runnable) {
        return runnable.runTask(plugin);
    }

    public BukkitTask sync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public BukkitTask async(BukkitRunnable runnable) {
        return runnable.runTaskAsynchronously(plugin);
    }

    public BukkitTask async(Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public BukkitTask later(BukkitRunnable runnable, long after) {
        return runnable.runTaskLater(plugin, after);
    }

    public BukkitTask later(Runnable runnable, long after) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, after);
    }

    public BukkitTask laterAsync(BukkitRunnable runnable, long after) {
        return runnable.runTaskLaterAsynchronously(plugin, after);
    }

    public BukkitTask laterAsync(Runnable runnable, long after) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, after);
    }

    public BukkitTask timer(BukkitRunnable runnable, long after, long repeat) {
        return runnable.runTaskTimer(plugin, after, repeat);
    }

    public BukkitTask timer(Runnable runnable, long after, long repeat) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, after, repeat);
    }

    public BukkitTask timerAsync(BukkitRunnable runnable, long after, long repeat) {
        return runnable.runTaskTimerAsynchronously(plugin, after, repeat);
    }

    public void cancel(int taskID) {
        plugin.getServer().getScheduler().cancelTask(taskID);
    }

}
