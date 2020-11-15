package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.util.UUID;

public class Oof implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "エラー！このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player player = (Player) sender;

        if (player.hasMetadata("x-hitter") && player.hasMetadata("x-hitted"))
        {
            String uuid = null;
            int v = 0;
            for (MetadataValue hitter: player.getMetadata("x-hitter"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    uuid = hitter.asString();
            for (MetadataValue hitter: player.getMetadata("x-hitted"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    v = hitter.asInt();
            player.removeMetadata("x-hitted", AreaPvP.getPlugin());
            player.removeMetadata("x-hitter", AreaPvP.getPlugin());
            if (v >= 8 && uuid != null)
            {
                Player killer = Bukkit.getPlayer(UUID.fromString(uuid));
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        PlayerInfo info = PlayerModify.getInfo(player);
                        int nm = 28;

                        if (info != null)
                            nm = nm * (info.prestige * 110 / 100);

                        PlayerModify.addExp(killer, nm);
                        killer.sendMessage(ChatColor.GREEN +
                                ChatColor.BOLD.toString() +
                                "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                                player.getDisplayName() +
                                ChatColor.AQUA + " +" + nm + "XP"
                        );
                    }
                }.runTaskAsynchronously(AreaPvP.getPlugin());
                sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!" + ChatColor.RESET + ChatColor.GRAY + " by " + killer.getDisplayName());
                player.spigot().respawn();
                return true;
            }

        }

        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        player.spigot().respawn();
        return true;
    }
}
