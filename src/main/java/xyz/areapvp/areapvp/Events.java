package xyz.areapvp.areapvp;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Events implements Listener
{
    @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
        e.getEntity().getPlayer().spigot().respawn();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e)
    {
        Entity ee = e.getEntity();
        if (!(ee instanceof Player))
            return;
        EntityDamageEvent.DamageCause type = e.getCause();
        if (type == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
    }

    @EventHandler
    private void onBreak(BlockBreakEvent e)
    {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (e.getBlock().hasMetadata("newPlayer"))
        {
            AreaPvP.blockPlace.remove(e.getBlock().getLocation());
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent e)
    {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        e.getBlock().setMetadata("newPlayer", new FixedMetadataValue(AreaPvP.getPlugin(), "binzyouozisan"));

        Integer remove; //消すまでの時間

        switch (e.getBlockPlaced().getType())
        {
            case COBBLESTONE:
                remove = 20; //丸石なら20秒後に削除
                break;
            case OBSIDIAN:
                remove = 180; //2分後
                break;
            default:
                remove = null; //削除しない
        }
        AreaPvP.blockPlace.put(e.getBlock().getLocation(), remove);
    }
}
