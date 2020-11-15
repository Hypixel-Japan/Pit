package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Kill implements Listener {
    @EventHandler
    public void killEvent(PlayerDeathEvent e) {
        e.getEntity( ).getPlayer( ).spigot( ).respawn( );


    }
}
