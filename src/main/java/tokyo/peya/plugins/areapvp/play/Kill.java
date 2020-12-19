package tokyo.peya.plugins.areapvp.play;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.scheduler.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.perk.*;
import tokyo.peya.plugins.areapvp.play.decoration.*;
import tokyo.peya.plugins.areapvp.player.*;

import java.math.*;
import java.util.*;

public class Kill
{
    private static final HashMap<UUID, Long> st = new HashMap<>();

    private static final LinkedHashMap<UUID, UUID> reduceKill = new LinkedHashMap<>();
    private static final LinkedHashMap<UUID, Long> reducer = new LinkedHashMap<>();

    public static Long getStreak(UUID player)
    {
        Long str = st.get(player);
        return str == null ? 0: str;
    }

    public static void setStreak(Player player, long st)
    {
        Kill.st.put(player.getUniqueId(), st);
        if (st % 5 == 0)
        {
            if (!InfoContainer.isInitialize(player))
                return;
            PlayerInfo info = InfoContainer.getInfoAllowNick(player);
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "STREAK! " +
                    ChatColor.RESET + ChatColor.GRAY + "of " + ChatColor.RED + st + ChatColor.GRAY +
                    " kills by " + PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " " +
                    player.getName()));
        }
    }

    public static void reset(Player player)
    {
        st.remove(player.getUniqueId());
    }

    public static void processKill(final Player deather)
    {
        processKill(null, deather);
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
            for (MetadataValue hitter : deather.getMetadata("x-hitter"))
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
            Player finalCK = cK;
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    PerkProcess.onKill(deather);
                    Sounds.play(finalCK, Sounds.SoundType.KILL);
                    process(finalCK, deather);
                    finalCK.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GRAY + deather.getName() + " " + ChatColor.GREEN + ChatColor.BOLD + "KILL!").create());
                }
            }.runTask(AreaPvP.getPlugin());
        }
    }

    public static boolean hasReduce(Player player)
    {
        Long reduce = reducer.get(player.getUniqueId());

        boolean reduced = false;

        if (reduce != null && reduce > 30)
            reduced = true;

        return reduced;
    }

    private static void reduce(Player kill, Player death)
    {
        if (reduceKill.get(kill.getUniqueId()) == death.getUniqueId())
            reducer.merge(kill.getUniqueId(), 1L, Long::sum);
        else
        {
            reduceKill.put(kill.getUniqueId(), death.getUniqueId());
            reducer.put(kill.getUniqueId(), 1L);
        }
    }

    private static void process(Player killer, Player deather)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {

                if (!InfoContainer.isInitialize(deather))
                    return;

                PlayerInfo info = InfoContainer.getInfo(deather);


                reduce(killer, deather);
                boolean reduced = hasReduce(killer);

                long exp = Exp.calcKillExp(killer, deather, info.level, info.prestige);

                double money = 12;
                money = money * ((info.prestige == 0 ? 1: info.prestige) * 120d / 100d);
                BigDecimal bd = new BigDecimal(money);
                bd = bd.setScale(3, BigDecimal.ROUND_DOWN);

                long bonus = 0;
                if (getStreak(killer.getUniqueId()) > 40)
                    bonus += new Random().nextInt(50) + 40;
                else if (getStreak(killer.getUniqueId()) + 1 > 5)
                    bonus += new Random().nextInt(7);

                if (Perk.contains(killer, "streaker"))
                    exp += (bonus * 3);
                else
                    exp += bonus;
                bd = bd.add(new BigDecimal(exp).multiply(new BigDecimal(5)));

                money = bd.doubleValue();

                if (reduced)
                {
                    money = 0.0;
                    exp = 0;
                }

                AreaPvP.economy.depositPlayer(killer, money);
                PlayerModify.addExp(killer, exp);

                info = InfoContainer.getInfoAllowNick(deather);

                String name = PlayerInfo.getPrefix(info.level, info.prestige) +
                        ChatColor.GRAY + " " + deather.getName();


                killer.sendMessage(ChatColor.GREEN +
                        ChatColor.BOLD.toString() +
                        "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                        name +
                        ChatColor.AQUA + " +" + exp + "XP " +
                        ChatColor.GOLD + " +" + money + "g" +
                        (reduced ? ChatColor.GRAY + " (reduced)": "")
                );

                PlayerInfo kInfo = InfoContainer.getInfoAllowNick(killer);

                long streak = getStreak(killer.getUniqueId());
                if (!reduced)
                    st.put(killer.getUniqueId(), streak + 1);

                if (!InfoContainer.isInitialize(killer))
                {
                    unknownRestart(deather);
                    return;
                }

                if (!reduced && (streak + 1) % 5 == 0)
                {
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "STREAK! " +
                            ChatColor.RESET + ChatColor.GRAY + "of " + ChatColor.RED + (streak + 1) + ChatColor.GRAY +
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
