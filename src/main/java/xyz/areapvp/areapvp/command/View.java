package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.areapvp.areapvp.ProfileViewer;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class View implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.RED + "引数が不足しています。使用法: /view <PlayerName>");
            return true;
        }

        PlayerInfo info = PlayerModify.getInfo((Player) sender);
        if (info == null)
            return true;
        if (info.level < 20)
        {
            sender.sendMessage(ChatColor.RED + "この機能はまだ使用できません！");
            return true;
        }

        AtomicReference<OfflinePlayer> player = new AtomicReference<>();
        player.set(Bukkit.getPlayer(args[0]));
        if (player.get() == null)
        {
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(o -> player.set(o.getName().equals("") ? Bukkit.getOfflinePlayer(o.getUniqueId()): null));
            if (player.get() == null)
            {
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした。");
                return true;
            }
        }

        ProfileViewer.viewPlayer(player.get().getPlayer(), (Player) sender);

        return true;
    }
}
