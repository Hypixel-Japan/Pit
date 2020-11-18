package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.InventoryUtils;
import xyz.areapvp.areapvp.KillStreak;
import xyz.areapvp.areapvp.level.Exp;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.math.BigDecimal;
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
        if (player.getLocation().getY() >= AreaPvP.config.getInt("spawnLoc"))
        {
            player.sendMessage(ChatColor.RED + "あなたは現在スポーンポイント範囲内にいます！");
            return true;
        }

        if (player.hasMetadata("x-spawn"))
        {
            sender.sendMessage(ChatColor.RED + "エラー！/respawnは15秒に1回可能です！");
            return true;
        }

        player.setMetadata("x-spawn", new FixedMetadataValue(AreaPvP.getPlugin(), 15));

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

                        if (info == null)
                            return;

                        long nm = Exp.calcKillExp(killer, (Player) sender, info.level, info.prestige);

                        nm = (int) (nm * ((info.prestige == 0 ? 1: info.prestige * 0.5)));

                        double gse = 11;

                        gse = gse * ((info.prestige == 0 ? 1: info.prestige) * 110d / 100d);
                        BigDecimal bd = new BigDecimal(gse);

                        bd = bd.setScale(3, BigDecimal.ROUND_DOWN);

                        gse = bd.doubleValue();

                        AreaPvP.economy.depositPlayer(killer, gse);
                        PlayerModify.addExp(killer, nm);

                        PlayerInfo fs = PlayerModify.getInfo(player);
                        String name = ChatColor.GRAY + "[1] " + player.getDisplayName();
                        if (fs != null)
                            name = PlayerInfo.getPrefix(fs.level, fs.prestige) + ChatColor.GRAY + " " + player.getDisplayName();

                        killer.sendMessage(ChatColor.GREEN +
                                ChatColor.BOLD.toString() +
                                "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                                name +
                                ChatColor.AQUA + " +" + nm + "XP " +
                                ChatColor.GOLD + " +" + gse + ".00g"
                        );
                        KillStreak.kill(killer);
                        sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!" + ChatColor.RESET + ChatColor.GRAY + " by " +
                                PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " " + killer.getDisplayName());

                    }
                }.runTaskAsynchronously(AreaPvP.getPlugin());
                player.teleport(player.getWorld().getSpawnLocation());
                InventoryUtils.reItem(player);
                return true;
            }

        }

        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        player.teleport(player.getWorld().getSpawnLocation());
        InventoryUtils.reItem(player);

        return true;
    }
}
