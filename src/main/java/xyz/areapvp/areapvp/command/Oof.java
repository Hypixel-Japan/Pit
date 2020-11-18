package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.InventoryUtils;
import xyz.areapvp.areapvp.Kill;

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
                Kill.processKill(killer, (Player) sender);
                return true;
            }

        }

        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        player.teleport(player.getWorld().getSpawnLocation());
        InventoryUtils.reItem(player);

        return true;
    }
}
