package tokyo.peya.plugins.areapvp.command;

import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.scheduler.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.inventory.*;
import tokyo.peya.plugins.areapvp.play.*;
import tokyo.peya.plugins.areapvp.player.*;

import java.util.*;

public class PitDebug implements CommandExecutor
{

    private static void shop(Player player, String[] args)
    {
        String type = args[1];
        switch (type)
        {
            case "item":
                Shop.openInventory(player);
                AreaPvP.gui.put(player.getUniqueId(), "item");
                break;
            case "perk":
                AreaPvP.gui.put(player.getUniqueId(), "firstPerk");
                Shop.openPerkInventory(player.getPlayer());
                break;
            default:
                player.sendMessage("エラー：不正なタイプ");
        }
    }

    private static void metaData(Player player, String[] args)
    {
        String type = args[1];
        if (!type.equals("add") && !type.equals("remove") && !type.equals("list"))
        {
            player.sendMessage(ChatColor.RED + "エラー：タイプが無効です。");
            return;
        }

        String data = null;
        if (args.length > 2)
            data = args[2];
        String value = null;
        if (args.length > 3)
            value = args[3];

        if (data == null && value == null && type.equals("remove"))
        {
            player.sendMessage(ChatColor.RED + "エラー：引数が不足しています！");
            return;
        }

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
            else if (type.equals("remove"))
                player.getInventory().setItemInMainHand(Items.removeMetadata(player.getInventory().getItemInMainHand(), data));
            else
            {
                boolean[] flag = {false};
                Items.getMetadataList(player.getInventory().getItemInMainHand())
                        .forEach((s, s2) -> {

                            ComponentBuilder builder =
                                    new ComponentBuilder(ChatColor.GREEN + s + "   " + ChatColor.AQUA + "->" +
                                            ChatColor.LIGHT_PURPLE + "   " + s2.replace("\n", "\n" + ChatColor.LIGHT_PURPLE));
                            builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2.replace("\n", "")));

                            player.spigot().sendMessage(builder.create());
                            flag[0] = true;
                        });
                if (!flag[0])
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "No metadata(s) founded.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void changeDamage(Player player)
    {
        boolean meta = player.hasMetadata("damageDebug");
        if (!meta)
            player.setMetadata("damageDebug", new FixedMetadataValue(AreaPvP.getPlugin(), "1b"));
        else
            player.removeMetadata("damageDebug", AreaPvP.plugin);
        player.sendMessage((meta ? ChatColor.RED: ChatColor.GREEN) + "ダメージデバッグを" + (meta ? "無効化": "有効化") + "しました。");
    }

    public static Long parseLong(String str)
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
            case "debug":
                AreaPvP.debugging = !AreaPvP.debugging;
                sender.sendMessage(ChatColor.GREEN + "デバッグモード：" + AreaPvP.debugging);
                break;
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
                if (args.length != 1)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                changeDamage(player);
                break;
            case "meta":
                if (args.length > 4 || args.length < 2)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                metaData(player, args);
                break;
            case "evacuate":
            case "ev":
                player.teleport(player.getWorld().getSpawnLocation());
                ((Player) sender).sendTitle(ChatColor.RED + "EVACUATE!", "", 10, 20, 10);
                break;
            case "shop":
                if (args.length != 2)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                shop(player, args);
                break;
            case "streak":
                if (args.length != 2)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数の大きさが不正です！");
                    return true;
                }
                Long l;
                if ((l = parseLong(args[1])) == null)
                {
                    sender.sendMessage(ChatColor.RED + "エラー：引数が数値ではありません！");
                    return true;
                }
                Kill.setStreak(player, l);
                break;
        }

        return true;
    }

}
