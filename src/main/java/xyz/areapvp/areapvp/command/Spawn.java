package xyz.areapvp.areapvp.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        player.teleport(player.getWorld().getSpawnLocation());

        return true;
    }
}
