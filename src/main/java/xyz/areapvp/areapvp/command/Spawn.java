package xyz.areapvp.areapvp.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.areapvp.areapvp.AreaPvP;

public class Spawn implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "エラー！このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player player = (Player) sender;
        if (player.hasMetadata("x-hitted"))
        {
            sender.sendMessage(ChatColor.RED + "エラー！戦闘中は/respawnできません！");
            return true;
        }

        if (player.hasMetadata("x-spawn"))
        {
            sender.sendMessage(ChatColor.RED + "エラー！/respawnは15秒に1回可能です！");
            return true;
        }
        if (player.getLocation().getY() >= AreaPvP.spawnloc)
        {
            player.sendMessage(ChatColor.RED + "あなたは現在スポーンポイント範囲内にいます！");
            return true;
        }
        if (player.getEyeLocation().getY() >= AreaPvP.spawnloc)
            player.sendMessage(ChatColor.RED + "あなたは現在スポーンポイント範囲内にいます！");
        else
        {
            player.teleport(player.getWorld().getSpawnLocation());
            player.setMetadata("x-spawn", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        }
        return true;
    }
}
