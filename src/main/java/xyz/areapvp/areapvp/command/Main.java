package xyz.areapvp.areapvp.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
            case 3:
                if (args[0].equals("level") || args[0].equals("lv"))
                {
                    int lv;
                    try
                    {
                        lv = Integer.parseInt(args[1]);
                    }
                    catch (Exception ignored)
                    {
                        sender.sendMessage(ChatColor.RED + "エラー！引数が数字ではありません！");
                        return true;
                    }
                    Player player = Bukkit.getPlayer(args[2]);
                    if (player == null)
                    {
                        sender.sendMessage(ChatColor.RED + "エラー！プレイヤーが見つかりませんでした！");
                        return true;
                    }
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            PlayerInfo info = PlayerModify.getInfo(player);
                            if (info == null)
                            {
                                sender.sendMessage(ChatColor.RED + "エラー！プレイヤーはアカウントを持っていません！");
                                return;
                            }

                            int level = info.level + lv;
                            if (level < 1)
                            {
                                sender.sendMessage(ChatColor.RED + "エラー！レベルが負の値です！");
                            }

                            PlayerModify.addLevel(player, level, 0);
                        }
                    }.runTaskAsynchronously(AreaPvP.getPlugin());
                }
        }

        return true;
    }
}
