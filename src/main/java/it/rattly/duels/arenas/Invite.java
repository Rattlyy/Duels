package it.rattly.duels.arenas;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
public class Invite {
    private final UUID sender;
    private final UUID target;

    @Nullable private String kit;
    private BukkitTask cancelTask;

    public Invite(UUID sender, UUID target) {
        this.sender = sender;
        this.target = target;
    }
}
