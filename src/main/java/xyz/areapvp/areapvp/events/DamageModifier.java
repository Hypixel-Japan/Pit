package xyz.areapvp.areapvp.events;

import kernitus.plugin.OldCombatMechanics.utilities.damage.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.*;
import xyz.areapvp.areapvp.*;

import java.util.*;

public class DamageModifier implements Listener
{

    @EventHandler
    public void onOCM(OCMEntityDamageByEntityEvent e)
    {
        if (!(e.getDamager() instanceof Player))
            return;
        Player hitter = (Player) e.getDamager();
        e.setBaseDamage(Items.getDamage(hitter.getInventory().getItemInMainHand()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamageByEntityOnModified(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;


        if (((Player) e.getDamager()).getGameMode() == GameMode.CREATIVE)
        {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity().getLocation().getY() >= AreaPvP.spawnloc || e.getDamager().getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            return;
        }

        Player hitter = (Player) e.getDamager();

        Player damager = (Player) e.getEntity();

        hitter.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitter", new FixedMetadataValue(AreaPvP.getPlugin(), hitter.getUniqueId().toString()));

        if (!hitter.hasMetadata("damageDebug"))
            hitter.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(ChatColor.GRAY + damager.getName() + " "
                            + Events.getDamageIndicator(e.getDamage(), damager)).create()
            );
        else
            hitter.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(ChatColor.RED.toString() + e.getDamage() + " => " + damager.getHealth()).create()
            );

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;


        if (((Player) e.getDamager()).getGameMode() == GameMode.CREATIVE)
        {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity().getLocation().getY() >= AreaPvP.spawnloc || e.getDamager().getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            return;
        }

        Player hitter = (Player) e.getDamager();

        if (hitter.getInventory().getItemInMainHand() == null)
            e.setDamage(new Random().nextInt(1) + 0.5d);
        else
            e.setDamage(Items.getDamage(hitter.getInventory().getItemInMainHand()));
    }


}
