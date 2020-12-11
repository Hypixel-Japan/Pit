package xyz.areapvp.areapvp;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;
import xyz.areapvp.areapvp.item.Items;
import xyz.areapvp.areapvp.item.items.*;
import xyz.areapvp.areapvp.perk.*;
import xyz.areapvp.areapvp.perk.perks.*;
import xyz.areapvp.areapvp.play.*;
import xyz.areapvp.areapvp.play.decoration.*;
import xyz.areapvp.areapvp.player.*;

import java.sql.*;
import java.util.*;

public class Init
{
    public static void schedulePlayerTimer()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Bukkit.getOnlinePlayers()
                        .forEach(player -> {
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 810, 7, true));
                            Sidebar.sendBoard(player);
                            Scoreboard board = player.getScoreboard();

                            if (board == null)
                                board = Bukkit.getScoreboardManager().getNewScoreboard();
                            Team t = board.getTeam("c");

                            if (t == null)
                                t = board.registerNewTeam("c");

                            if (t != null)
                            {
                                t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                                if (!t.getEntries().contains(player.getName()))
                                    t.addEntry(player.getName());
                                if (!InfoContainer.isInitialize(player))
                                    return;
                                PlayerInfo info = InfoContainer.getInfo(player);
                                t.setPrefix(PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.WHITE);
                            }

                            player.setScoreboard(board);
                        });
            }
        }.runTaskTimer(AreaPvP.plugin, 0L, 20L);
    }

    public static void scheduleSpawnTimer()
    {
        int b = AreaPvP.config.getInt("spawnLoc");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AreaPvP.getPlugin(), () -> Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    int y = (int) player.getLocation().getY();
                    if (y >= b)
                    {
                        player.removeMetadata("x-hitter", AreaPvP.getPlugin());
                        player.removeMetadata("x-hitted", AreaPvP.getPlugin());
                        player.removeMetadata("x-streak", AreaPvP.getPlugin());
                        Kill.reset(player);
                        player.setHealth(20);

                    }
                }), 0L, 1L);
    }

    public synchronized static void arrowTimer()
    {
        Bukkit.getScheduler().runTaskTimer(AreaPvP.plugin, () -> {
            final LinkedList<UUID> removal = new LinkedList<>();
            AreaPvP.arrows.forEach((k, v) -> {
                v = v - 1;

                Entity arrow = Bukkit.getEntity(k);

                if (arrow == null)
                {
                    removal.add(k);
                    return;
                }

                if (arrow.isDead())
                {
                    removal.add(k);
                    return;
                }
                if (v < 0)
                {
                    arrow.remove();
                    removal.add(k);
                    return;
                }

                AreaPvP.arrows.put(k, v);

            });
            removal.parallelStream().forEach(arrow -> AreaPvP.arrows.remove(arrow));

        }, 0L, 10L);
    }

    static void initDatabase()
    {
        try (Statement statement = AreaPvP.data.getConnection().createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS player(" +
                    "UUID text," +
                    "LEVEL integer," +
                    "PRESTIGE integer," +
                    "EXP bigint" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS perk(" +
                    "UUID text," +
                    "PERK text" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS holdperk(" +
                    "UUID text," +
                    "PERK text" +
                    ")");
        }
        catch (Exception ignored)
        {

        }
    }

    static void initShop()
    {
        xyz.areapvp.areapvp.item.Items.newLine();
        xyz.areapvp.areapvp.item.Items.addItem(new DiamondSword());
        xyz.areapvp.areapvp.item.Items.addItem(new Obsidian());
        xyz.areapvp.areapvp.item.Items.addItem(new ItemAir());
        xyz.areapvp.areapvp.item.Items.addItem(new DiamondChestPlate());
        xyz.areapvp.areapvp.item.Items.addItem(new DiamondBoots());
        xyz.areapvp.areapvp.item.Items.addItem(new ItemAir());
        xyz.areapvp.areapvp.item.Items.addItem(new ItemAir());
        Items.newLine();

        Perks.addPerk(new GoldenHead());
        Perks.addPerk(new FishingRod());
        Perks.addPerk(new EndlessQuiver());
        Perks.addPerk(new MineMan());
        Perks.addPerk(new SafetyFirst());
        Perks.addPerk(new Streaker());
        Perks.addPerk(new Vampire());

    }
}
