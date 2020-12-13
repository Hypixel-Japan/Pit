package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.areapvp.areapvp.inventory.ProfileViewer;
import xyz.areapvp.areapvp.player.*;

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

        if (!InfoContainer.isInitialize((Player) sender))
            return true;
        PlayerInfo info = InfoContainer.getInfo((Player) sender);
        if (info.prestige == 0 && info.level < 70)
        {
            sender.sendMessage(ChatColor.RED + "この機能はまだ使用できません！");
            return true;
        }

        AtomicReference<OfflinePlayer> player = new AtomicReference<>();
        player.set(Bukkit.getPlayer(args[0]));
        if (player.get() == null)
        {
            player.set(Bukkit.getOfflinePlayer(args[0]));
            if (player.get() == null)
            {
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした。");
                return true;
            }
        }

        OfflinePlayer atp = player.get();

        if (atp.isOnline() && InfoContainer.isNicked(atp.getPlayer()))
        {
            sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "HOAH! " +
                    ChatColor.RESET + ChatColor.GRAY + "このプレイヤーは /view を無効化しています！");
            return true;
        }

        ProfileViewer.viewPlayer(atp.getPlayer(), (Player) sender);

        return true;
    }
}
