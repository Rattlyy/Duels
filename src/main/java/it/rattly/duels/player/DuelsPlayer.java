package it.rattly.duels.player;

import it.rattly.duels.Duels;
import it.rattly.duels.arenas.Invite;
import it.rattly.duels.utils.Palette;
import lombok.Getter;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public final class DuelsPlayer {
    @Getter
    private final UserData data;
    @Getter
    private final Player player;
    @Getter
    private final Duels duels;

    private HashMap<UUID, Invite> invites;

    public DuelsPlayer(UserData data, Player player, Duels duels) {
        this.data = data;
        this.player = player;
        this.duels = duels;
    }

    public Optional<Invite> getInviteBy(UUID sender) {
        return Optional.ofNullable(invites.get(sender));
    }

    public boolean isInvitedBy(UUID uuid) {
        return invites.containsKey(uuid);
    }

    public void invite(Player sender) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5f, 1f);
        player.sendMessage(
                text().append(
                        text("You have been invited to fight with ").color(NamedTextColor.GREEN),
                        text(sender.getName()).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                        text("!"),
                        newline(),
                        text("This invitation will expire in 30 seconds."),
                        newline(), newline(),
                        text("[ACCEPT]").color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/duelaccept " + sender.getName())),
                        text(" "),
                        text("[DENY]").color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/dueldeny " + sender.getName()))
                )
        );

        Invite invite = new Invite(sender.getUniqueId(), data.getUuid());
        invites.put(sender.getUniqueId(), invite);

        invite.setCancelTask(duels.getScheduler().later(() -> {
            if (!player.isOnline())
                return;

            player.sendMessage(
                    text().append(
                            text("Your invite by "),
                            text(sender.getName()).decorate(TextDecoration.BOLD),
                            text(" expired.")
                    ).color(Palette.ERROR)
            );

            invites.remove(player.getUniqueId());
        }, 20 * 30));
    }

    public void setWins(int wins) {
        data.setWins(wins);

        updateFields(List.of("wins"), wins);
    }

    public void setLoss(int loss) {
        data.setKills(loss);

        updateFields(List.of("loss"), loss);
    }

    public void setKills(int kills) {
        data.setKills(kills);

        updateFields(List.of("kills"), kills);
    }

    public void setDeaths(int deaths) {
        data.setKills(deaths);

        updateFields(List.of("deaths"), deaths);
    }

    public void setWinstreak(int winstreak) {
        data.setKills(winstreak);

        updateFields(List.of("winstreak"), winstreak);
    }

    private void updateFields(List<String> fields, Object... values) {
        StringBuilder sql = new StringBuilder("UPDATE players SET ");

        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i)).append(" = ?");

            if (i != (fields.size() - 1)) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE uuid = ?");

        duels.getDatabaseProvider().async(() -> {
            try (Connection conn = duels.getDatabaseProvider().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                int index = 0;
                for (Object value : values) {
                    index++;
                    stmt.setObject(index, value);
                }

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
