package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.models.user.RUser;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Slf4j
public final class ScoreboardTeam {
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<RUser> members = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final ScoreboardContent scoreboard;

    @Getter(onMethod = @__(@NotNull))
    private final Map<Team.Option, Team.OptionStatus> options = new HashMap<>();

    @Getter(onMethod = @__(@NotNull))
    private final Team bukkitTeam;

    @Getter(onMethod = @__(@Nullable))
    @Setter(onParam = @__(@NotNull))
    private String prefix, suffix;

    @Getter
    @Setter
    private boolean friendlyFire, canSeeFriendlyInvisibles;

    ScoreboardTeam(@NotNull String name, @NotNull ScoreboardContent scoreboard) {
        this.name = name;
        this.scoreboard = scoreboard;
        bukkitTeam = scoreboard.getBukkitScoreboard().registerNewTeam(ChatColor.stripColor(name));
    }

//    TODO
    public void update() {
        bukkitTeam.displayName(Component.text(name));

        bukkitTeam.setAllowFriendlyFire(friendlyFire);
        bukkitTeam.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles);

        if(prefix != null) {
            bukkitTeam.prefix(Component.text(prefix));
        }

        if(suffix != null) {
            bukkitTeam.suffix(Component.text(suffix));
        }


//        Update all Options
        for(Map.Entry entry : options.entrySet()) {
            final Team.Option option = (Team.Option) entry.getKey();
            final Team.OptionStatus status = (Team.OptionStatus) entry.getValue();

            bukkitTeam.setOption(option, status);
        }

//        Clear all Members
        final Set<String> entries = bukkitTeam.getEntries();
        for(String entry : entries) {
            bukkitTeam.removeEntry(entry);
        }

//        Add new Members
        for(RUser user : members) {
            bukkitTeam.addPlayer(user.getPlayer());
        }
    }

    public void setOption(@NotNull Team.Option option, @NotNull Team.OptionStatus status) {
        options.put(option, status);
    }

    public void addMember(@NotNull RUser user) {
        if(members.contains(user)) {
            return;
        }

        members.add(user);

        update();
    }

    public void removeMember(@NotNull RUser user) {
        if(!members.contains(user)) {
            return;
        }

        members.remove(user);

        update();
    }
}
