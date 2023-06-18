package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Slf4j
public final class ScoreboardTeam {
    /**
     * the name of this scoreboard team
     */
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    /**
     * the members of this scoreboard team
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<RUser> members = new ArrayList<>();

    /**
     * the scoreboard content this scoreboard team belongs to
     */
    @Getter(onMethod = @__(@NotNull))
    private final ScoreboardContent scoreboard;

    /**
     * the scoreboard team options of this scoreboard team
     */
    @Getter(onMethod = @__(@NotNull))
    private final Map<Team.Option, Team.OptionStatus> options = new HashMap<>();

    /**
     * the bukkit instance this scoreboard team represents
     */
    @Getter(onMethod = @__(@NotNull))
    private final Team bukkitTeam;

    /**
     * the prefix of this scoreboard team
     */
    @Getter(onMethod = @__(@Nullable))
    @Setter(onParam = @__(@NotNull))
    private String prefix;

    /**
     * the suffix of this scoreboard team
     */
    @Getter(onMethod = @__(@Nullable))
    @Setter(onParam = @__(@NotNull))
    private String suffix;

    @Getter
    @Setter
    private boolean friendlyFire;

    /**
     * when true, each scoreboard team member can see each other disregarding any invisibility
     */
    @Getter
    @Setter
    private boolean canSeeFriendlyInvisibles;

    ScoreboardTeam(@NotNull String name, @NotNull ScoreboardContent scoreboard) {
        this.name = name;
        this.scoreboard = scoreboard;
        this.bukkitTeam = scoreboard.getBukkitScoreboard().registerNewTeam(ChatColor.stripColor(name));
    }

    /**
     * updates this scoreboard team
     */
    public void update() {
        this.bukkitTeam.setDisplayName(this.name);
        this.bukkitTeam.setAllowFriendlyFire(this.friendlyFire);
        this.bukkitTeam.setCanSeeFriendlyInvisibles(this.canSeeFriendlyInvisibles);

        if(this.prefix != null) {
            this.bukkitTeam.setPrefix(this.prefix);
        }

        if(this.suffix != null) {
            this.bukkitTeam.setSuffix(this.suffix);
        }


        // Update all Options
        for(Map.Entry<Team.Option, Team.OptionStatus> entry : this.options.entrySet()) {
            final Team.Option option = entry.getKey();
            final Team.OptionStatus status = entry.getValue();

            this.bukkitTeam.setOption(option, status);
        }

        // Clear all Members
        final Set<String> entries = this.bukkitTeam.getEntries();

        for(String entry : entries) {
            this.bukkitTeam.removeEntry(entry);
        }

        // Add new Members
        for(RUser user : this.members) {
            this.bukkitTeam.addPlayer(user.getPlayer());
        }
    }

    /**
     * sets the option of this scoreboard team to those specified
     * @param option the team option
     * @param status the team option status
     */
    public void setOption(@NotNull Team.Option option, @NotNull Team.OptionStatus status) {
        this.options.put(option, status);
    }

    /**
     * adds the user specified to this scoreboard team
     * @param user the user
     */
    public void addMember(@NotNull RUser user) {
        if(this.members.contains(user)) {
            return;
        }

        this.members.add(user);
        update();
    }

    /**
     * removes the user specified from this scoreboard team
     * @param user the user
     */
    public void removeMember(@NotNull RUser user) {
        if(!this.members.contains(user)) {
            return;
        }

        this.members.remove(user);
        update();
    }
}
