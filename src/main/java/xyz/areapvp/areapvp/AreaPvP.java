package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AreaPvP extends JavaPlugin {
    public static FileConfiguration config;
    public static boolean Build = false;
    public static AreaPvP plugin;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager( ).registerEvents( new Block() , this );
        Bukkit.getPluginManager( ).registerEvents( new Kill() , this);
        Bukkit.getPluginManager( ).registerEvents( new PlayerJoin() , this);
        Bukkit.getPluginManager().registerEvents(  new NoFall() , this);
        getCommand( "areapvp" ).setExecutor( new Commands() );
        saveDefaultConfig();
        plugin = this;
        config = getConfig();
    }
    public static AreaPvP getPlugin()
    {
        return plugin;
    }
}
