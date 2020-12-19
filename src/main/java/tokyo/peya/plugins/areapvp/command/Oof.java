package tokyo.peya.plugins.areapvp.command;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.play.*;

import java.util.*;

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
        if (player.getLocation().getY() >= AreaPvP.spawnloc)
        {
            player.sendMessage(ChatColor.RED + "あなたは現在スポーンポイント範囲内にいます！");
            return true;
        }

        if (player.hasMetadata("x-spawn"))
        {
            sender.sendMessage(ChatColor.RED + "エラー！/respawnは10秒に1回可能です！");
            return true;
        }

        player.setMetadata("x-spawn", new FixedMetadataValue(AreaPvP.getPlugin(), 10));

        if (player.hasMetadata("x-hitter") && player.hasMetadata("x-hitted"))
        {
            String uuid = null;
            int v = 0;
            for (MetadataValue hitter : player.getMetadata("x-hitter"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    uuid = hitter.asString();
            for (MetadataValue hitter : player.getMetadata("x-hitted"))
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
