package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.level.Exp;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class Kill
{
    private static final HashMap<UUID, Long> st = new HashMap<>();

    public static Long getStreak(UUID player)
    {
        Long str = st.get(player);
        return str == null ? 0: str;
    }


    public static void reset(Player player)
    {
        st.remove(player.getUniqueId());
    }

    public static void processKill(final Player killer, final Player deather)
    {
        if (deather.isDead())
            deather.spigot().respawn();
        else
            deather.teleport(deather.getWorld().getSpawnLocation());
        deather.sendTitle(ChatColor.RED + "YOU DIED", "", 10, 20, 10);
        Player cK = killer;
        if (cK == null)
        {

            if (!deather.hasMetadata("x-hitter"))
            {
                unknownRestart(deather);
                return;
            }

            String uuid = null;
            for (MetadataValue hitter: deather.getMetadata("x-hitter"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    uuid = hitter.asString();
            if (uuid == null)
            {
                unknownRestart(deather);
                return;
            }
            cK = Bukkit.getPlayer(UUID.fromString(uuid));
        }

        if (cK != null)
        {
            if (cK.getUniqueId() == deather.getUniqueId())
            {
                unknownRestart(deather);
                return;
            }

            process(cK, deather);
        }
    }

    private static void process(Player killer, Player deather)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                PlayerInfo info = PlayerModify.getInfo(deather);

                if (info == null)
                    return;

                long exp = Exp.calcKillExp(killer, deather, info.level, info.prestige);

                double money = 12;
                money = money * ((info.prestige == 0 ? 1: info.prestige) * 120d / 100d);
                BigDecimal bd = new BigDecimal(money);
                bd = bd.setScale(3, BigDecimal.ROUND_DOWN);

                money = bd.doubleValue();

                AreaPvP.economy.depositPlayer(killer, money);
                PlayerModify.addExp(killer, exp);

                String name = PlayerInfo.getPrefix(info.level, info.prestige) +
                        ChatColor.GRAY + " " + deather.getName();

                killer.sendMessage(ChatColor.GREEN +
                        ChatColor.BOLD.toString() +
                        "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                        name +
                        ChatColor.AQUA + " +" + exp + "XP " +
                        ChatColor.GOLD + " +" + money + "g"
                );

                PlayerInfo kInfo = PlayerModify.getInfo(killer);

                long streak = getStreak(killer.getUniqueId());
                st.put(killer.getUniqueId(), streak + 1);

                if (kInfo == null)
                {
                    unknownRestart(deather);
                    return;
                }

                if ((streak + 1) % 5 == 0)
                {
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "STREAK! " +
                            ChatColor.RESET + ChatColor.GRAY + "of " + ChatColor.RED + streak + 1 + ChatColor.GRAY +
                            " kills by " + PlayerInfo.getPrefix(kInfo.level, kInfo.prestige) + ChatColor.GRAY + " " +
                            killer.getName()));
                }

                deather.spigot().respawn();
                deather.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!" +
                        ChatColor.RESET + ChatColor.GRAY + " by " +
                        PlayerInfo.getPrefix(kInfo.level, kInfo.prestige) + " " + ChatColor.GRAY + killer.getName());
                InventoryUtils.reItem(deather);

            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());


    }

    private static void unknownRestart(Player player)
    {
        player.spigot().respawn();
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        InventoryUtils.reItem(player);
    }
}
