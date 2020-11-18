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

        switch (args.length)
        {
            case 1:
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
                break;
            case 2:
                switch (args[1])
                {
                    case "xp":
                    case "exp":
                        sender.sendMessage(ChatColor.RED + "引数が足りません！正しい使用法は、/areapvp xp <add|remove> <amount>  です！");
                        break;
                    case "gold":
                    case "g":
                        sender.sendMessage(ChatColor.RED + "引数が足りません！正しい使用法は、/areapvp gold <add|remove> <amount>  です！");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "不明なコマンドです！引数を確認してください！");
                }
            case 3:
                if (!args[0].equals("exp") && !args[0].equals("g") && !args[0].equals("xp") && !args[0].equals("gold"))
                {
                    sender.sendMessage(ChatColor.RED + "エラー：不明なコマンドです！引数を確認してください！");
                    return true;
                }

                long a;
                try
                {
                    a = Long.parseLong(args[2]);
                }
                catch (Exception ignored)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：第3引数が数字ではありません！");
                    return true;
                }

                Long finalA = a;
                switch (args[1])
                {
                    case "add":
                        boolean hasGold = args[0].equals("gold") || args[0].equals("g");
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                Bukkit.getOnlinePlayers().parallelStream().forEach((player) ->
                                {
                                    if (hasGold)
                                        AreaPvP.economy.depositPlayer(player, finalA);
                                    else
                                        PlayerModify.addExp(player, finalA);
                                });
                            }
                        }.runTaskAsynchronously(AreaPvP.getPlugin());


                        sender.sendMessage((hasGold ? ChatColor.GOLD.toString() + a + "g": ChatColor.AQUA.toString() + a + "xp") + " " +
                                ChatColor.GRAY + "をオンラインのプレイヤー全員にくばりました。");
                    break;
                    case "remove":
                        if (args[0].equals("exp") || args[0].equals("xp"))
                        {
                            sender.sendMessage(ChatColor.RED + "エラー：XPを減らすことはできません！");
                            return true;
                        }

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                Bukkit.getOnlinePlayers().parallelStream().forEach((player) ->
                                {
                                    AreaPvP.economy.withdrawPlayer(player, finalA);
                                });
                            }
                        }.runTaskAsynchronously(AreaPvP.getPlugin());
                        sender.sendMessage(ChatColor.GOLD.toString() + a + "g" + ChatColor.GRAY + "をオンラインのプレイヤー全員から強奪しました。");
                }
        }

        return true;
    }

}
