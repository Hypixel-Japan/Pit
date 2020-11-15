package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

public class Main implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("areapvp.admin"))
        {
            sender.sendMessage(ChatColor.RED + "エラー！あなたは権限を持っていません！");
            return true;
        }

        switch (args.length)
        {
            case 1:
                if (args[0].equals("shop"))
                {
                    if (!(sender instanceof Player))
                    {
                        sender.sendMessage(ChatColor.RED + "エラー！プレイヤーからのみ実行できます！");
                        return true;
                    }
                    ItemStack stack = new ItemStack(Material.STICK);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "Shop Creator 3000");
                    stack.setItemMeta(meta);
                    ((Player) sender).getInventory().addItem(stack);
                }
        }

        return true;
    }
}
