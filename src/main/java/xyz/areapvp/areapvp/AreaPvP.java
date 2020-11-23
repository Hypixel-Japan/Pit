package xyz.areapvp.areapvp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import xyz.areapvp.areapvp.command.Main;
import xyz.areapvp.areapvp.command.Oof;
import xyz.areapvp.areapvp.command.Spawn;
import xyz.areapvp.areapvp.item.Items;
import xyz.areapvp.areapvp.item.items.DiamondBoots;
import xyz.areapvp.areapvp.item.items.DiamondChestPlate;
import xyz.areapvp.areapvp.item.items.DiamondSword;
import xyz.areapvp.areapvp.item.items.ItemAir;
import xyz.areapvp.areapvp.item.items.Obsidian;
import xyz.areapvp.areapvp.perk.PerkProcess;

import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class AreaPvP extends JavaPlugin
{
    public static FileConfiguration config;
    public static AreaPvP plugin;
    public static HashMap<Location, Integer> blockPlace;
    public static HashMap<UUID, Integer> arrows; //宣伝
    public static Timer timer;
    public static HikariDataSource data;
    public static HashMap<UUID, String> gui;
    public static Economy economy;
    public static int spawnloc;

    public static AreaPvP getPlugin()
    {
        return plugin;
    }

    private static void initShop()
    {
        Items.newLine();
        Items.addItem(new DiamondSword());
        Items.addItem(new Obsidian());
        Items.addItem(new ItemAir());
        Items.addItem(new DiamondChestPlate());
        Items.addItem(new DiamondBoots());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.newLine();
    }

    private static void initDatabase()
    {
        try (Statement statement = data.getConnection().createStatement())
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

    @Override
    public void onEnable()
    {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(new PerkProcess(), this);
        Bukkit.getPluginManager().registerEvents(new GUI(), this);
        getCommand("areapvp").setExecutor(new Main());
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("oof").setExecutor(new Oof());
        saveDefaultConfig();
        config = getConfig();
        spawnloc = config.getInt("spawnLoc");

        blockPlace = new HashMap<>();
        arrows = new HashMap<>();
        gui = new HashMap<>();
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            System.out.println("Please install 1 plugin(s) [Vault]");
            getServer().getPluginManager().disablePlugin(this);
        }

        initShop();

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        economy = rsp.getProvider();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/" + "data.db");
        data = new HikariDataSource(config);
        initDatabase();


        timer = new Timer();
        timer.runTaskTimer(this, 0L, 20L); //1秒に1回実行

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Bukkit.getOnlinePlayers()
                        .forEach(player -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, true));
                            Scoreboard b = Sidebar.getBoard(player);
                            if (b == null)
                                return;
                            player.setScoreboard(b);
                        });
            }
        }.runTaskTimer(this, 0L, 20L);

        int b = getConfig().getInt("spawnLoc");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers()
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

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            final LinkedList<UUID> removal = new LinkedList<>();
            arrows.forEach((k, v) -> {
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

                arrows.put(k, v);

            });
            removal.parallelStream().forEach(arrow -> arrows.remove(arrow));

        }, 0L, 10L);

    }

    @Override
    public void onDisable()
    {
        if (data != null)
            data.close();
        blockPlace.keySet().forEach((b) -> b.getWorld().getBlockAt(b).setType(Material.AIR));
    }
}
