package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.util.Arrays;

public class PitDebug implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player) || !Arrays.asList(AreaPvP.debugger).contains(((Player) sender).getUniqueId().toString()))
        {
            sender.sendMessage(ChatColor.RED + "エラー：使用許諾がありません！");
            return true;
        }

        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
            return true;
        }

        Player player = (Player) sender;

        switch (args[0])
        {
            case "exp":
            case "xp":
            case "gold":
            case "g":
                if (args.length != 3)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                expMoney(sender, args);
                break;
            case "damage":
                if (args.length != 2)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                changeDamage(player, args);
                break;
            case "meta":
                if (args.length > 3 || args.length < 2)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                metaData(player, args);
                break;
            case "evacute":
                player.teleport(player.getWorld().getSpawnLocation());
                ((Player) sender).sendTitle(ChatColor.RED  + "EVACUTE!", "", 10, 20, 10);
                break;
        }

        return true;
    }

    private static void metaData(Player player, String[] args)
    {
        String type = args[0];
        if (!type.equals("add") && !type.equals("remove"))
        {
            player.sendMessage(ChatColor.RED + "エラー：タイプが無効です。");
            return;
        }

        String data = args[1];
        String value = args[2];

        if (value == null && type.equals("add"))
        {
            player.sendMessage(ChatColor.RED + "エラー：値は必要です！");
            return;
        }
        if (player.getInventory().getItemInMainHand() == null ||
                player.getInventory().getItemInMainHand().getType() == Material.AIR)
        {
            player.sendMessage(ChatColor.RED + "エラー：物を持って実行してください！");
            return;
        }

        try
        {
            if (type.equals("add"))
                player.getInventory().setItemInMainHand(Items.addMetaData(player.getInventory().getItemInMainHand(), data, value));
            else
                player.getInventory().setItemInMainHand(Items.removeMetadata(player.getInventory().getItemInMainHand(), data));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private static void changeDamage(Player player, String[] args)
    {
        Long a;
        if ((a = parseLong(args[1])) == null)
        {
            player.sendMessage(ChatColor.RED + "エラー：第2引数が数字ではありません！");
            return;
        }

        if (player.getInventory().getItemInMainHand() == null ||
                player.getInventory().getItemInMainHand().getType() == Material.AIR)
        {
            player.sendMessage(ChatColor.RED + "エラー：物を持って実行してください！");
            return;
        }

        try
        {
            player.getInventory().setItemInMainHand(Items.changeDamage(player.getInventory().getItemInMainHand(), a));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Long parseLong(String str)
    {
        try
        {
            return Long.parseLong(str);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }


    private static void expMoney(CommandSender sender, String[] args)
    {
        Long a;
        if ((a = parseLong(args[2])) == null)
        {
            sender.sendMessage(ChatColor.RED + "エラー：第3引数が数字ではありません！");
            return;
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
                    return;
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

}
