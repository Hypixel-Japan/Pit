package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class AreaPvP extends JavaPlugin
{
    public static FileConfiguration config;
    public static AreaPvP plugin;
    public static HashMap<Location, Integer> blockPlace;
    public static Timer timer;

    public static AreaPvP getPlugin()
    {
        return plugin;
    }

    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getCommand("areapvp").setExecutor(new Commands());
        saveDefaultConfig();
        plugin = this;
        config = getConfig();
        blockPlace = new HashMap<>();
        timer = new Timer();
        timer.runTaskTimer(this, 0L, 20L); //1秒に1回実行
    }
}
