package xyz.areapvp.areapvp;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoFall implements Listener{
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        Entity ee = e.getEntity();
        if(!(ee instanceof Player ))return;
        Player p = (Player) ee;
        DamageCause type = e.getCause();
        if(type == DamageCause.FALL){
            e.setCancelled(true);
        }
    }
}
