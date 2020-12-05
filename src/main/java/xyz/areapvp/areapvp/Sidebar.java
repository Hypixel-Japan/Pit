package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.areapvp.areapvp.level.Exp;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sidebar
{
    public static Scoreboard getBoard(Player player)
    {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

        final PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return null;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();
        final Objective objective = board.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "THE HYPIXEL PIT");

        objective.getScore(ChatColor.GRAY + format.format(new Date())).setScore(info.prestige == 0 ? 10: 11);
        objective.getScore(ChatColor.WHITE.toString()).setScore(info.prestige == 0 ? 9: 10);
        if (info.prestige != 0)
            objective.getScore(ChatColor.WHITE + "Prestige: " + PlayerInfo.getPrestigeString(info.prestige)).setScore(9);
        objective.getScore(ChatColor.WHITE + "Level: " + PlayerInfo.getPrefix(info.level, info.prestige)).setScore(8);

        long exp = Exp.getExp(info.level + (info.level == 119 ? 0: 1), info.prestige) - info.exp;

        objective.getScore(ChatColor.WHITE + (info.level != 120 ? "Needed": "") + " XP: " +
                ChatColor.AQUA + (info.level != 120 ? String.format("%,d", exp): "MAXED!")).setScore(7);

        objective.getScore(ChatColor.ITALIC.toString()).setScore(6);

        BigDecimal decimal = BigDecimal.valueOf(AreaPvP.economy.getBalance(player));

        String decimalOf;

        if (decimal.compareTo(new BigDecimal(1000)) <= 0)
            decimalOf = String.valueOf(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        else
            decimalOf = String.format("%,d", decimal.setScale(1, BigDecimal.ROUND_HALF_UP).longValue());

        objective.getScore(ChatColor.WHITE + "Gold: " + ChatColor.GOLD + decimalOf + "g").setScore(5);

        objective.getScore(ChatColor.YELLOW.toString()).setScore(4);
        if (!player.hasMetadata("x-hitted"))
            objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.GREEN + "Idling").setScore(3);
        else
        {
            Integer hitted = null;
            for (MetadataValue hitter : player.getMetadata("x-hitted"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    hitted = hitter.asInt();
            if (hitted == null)
                objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.GREEN + "Idling").setScore(3);
            else
            {
                if (hitted > 5)
                    objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.RED + "Fighting").setScore(3);
                else
                    objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.RED + "Fighting" + ChatColor.GRAY +
                            "(" + hitted + ChatColor.GRAY + ")").setScore(3);
            }
        }

        long streak = Kill.getStreak(player.getUniqueId());

        if (streak == 0)
            objective.getScore(ChatColor.BLUE.toString()).setScore(2);
        else
            objective.getScore("Streak: " + ChatColor.GREEN + streak).setScore(2);
        objective.getScore(ChatColor.BLACK.toString()).setScore(1);
        player.setPlayerListName(PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " " + player.getName());
        player.setDisplayName(PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " " + player.getName());
        if (board.getTeam("c") == null)
            board.registerNewTeam("c");
        if (board.getTeam("c") != null)
        {
            board.getTeam("c").setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            if (!board.getTeam("c").getEntries().contains(player.getName()))
                board.getTeam("c").addEntry(player.getName());

        }

        player.setLevel(info.level);
        player.setExp(new BigDecimal(info.exp).divide(new BigDecimal(Exp.getExp(info.level, info.prestige) == 0 ? info.exp: Exp.getExp(info.level, info.prestige)), BigDecimal.ROUND_DOWN).floatValue());

        return board;
    }

}
