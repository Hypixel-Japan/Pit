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

        if (args.length != 1)
        {
            sender.sendMessage(ChatColor.RED + "エラー！ 引数の数が不正です！");
        }

            switch (args[0])
            {
                case "shop":
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
                    break;
                }
                case "perk":
                {
                    if (!(sender instanceof Player))
                    {
                        sender.sendMessage(ChatColor.RED + "エラー！プレイヤーからのみ実行できます！");
                        return true;
                    }
                    ItemStack stack = new ItemStack(Material.STICK);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "PerkShop Creator 3000");
                    stack.setItemMeta(meta);
                    ((Player) sender).getInventory().addItem(stack);
                    break;
                }
                case "exp":
                case "xp":
                    sender.sendMessage(ChatColor.RED + "引数が足りません！正しい使用法は、/areapvp xp <add|remove> <amount>  です！");
                    break;
                case "gold":
                case "g":
                    sender.sendMessage(ChatColor.RED + "引数が足りません！正しい使用法は、/areapvp gold <add|remove> <amount>  です！");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "不明なコマンドです！引数を確認してください！");
            }


        return true;
    }

}
