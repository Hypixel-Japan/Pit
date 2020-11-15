package xyz.areapvp.areapvp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.command.Main;
import xyz.areapvp.areapvp.command.Oof;
import xyz.areapvp.areapvp.command.Spawn;

import java.sql.Statement;
import java.util.HashMap;

public class AreaPvP extends JavaPlugin
{
    public static FileConfiguration config;
    public static AreaPvP plugin;
    public static HashMap<Location, Integer> blockPlace;
    public static Timer timer;
    public static HikariDataSource data;

    public static AreaPvP getPlugin()
    {
        return plugin;
    }

    @Override
    public void onEnable()
    {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getCommand("areapvp").setExecutor(new Main());
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("oof").setExecutor(new Oof());
        saveDefaultConfig();
        config = getConfig();
        blockPlace = new HashMap<>();

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
                        .forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 4, false)));
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable()
    {
        if (data != null)
            data.close();
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
        }
        catch (Exception ignored)
        {

        }
    }
}
