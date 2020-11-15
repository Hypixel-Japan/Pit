package xyz.areapvp.areapvp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.areapvp.areapvp.level.PlayerInfo;

public class PlayerEditor
{
    public static void changePlayerHead(Player player, int prestige, int level, Type type)
    {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(String.valueOf(level));
        if (team == null)
        {
            scoreboard.registerNewTeam(String.valueOf(level));
            team = scoreboard.getTeam(String.valueOf(level));
            if (team == null)
                return;
        }

        team.setPrefix(PlayerInfo.getPrefix(level, prestige) + ChatColor.GRAY + " ");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

        switch (type)
        {
            case CREATE:
                team.addEntry(player.getName());
                break;
            case UPDATE:
                team.removeEntry(player.getName());
                changePlayerHead(player, prestige, level, Type.CREATE);
                break;
        }
    }

    public enum Type
    {
        CREATE, UPDATE, DELETE
    }
}
