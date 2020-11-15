package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.util.UUID;

public class Events implements Listener
{
    @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
        Player killer = e.getEntity().getKiller();

        if (killer == null)
        {
            if (e.getEntity().hasMetadata("x-hitter"))
            {
                String uuid = null;
                for (MetadataValue hitter: e.getEntity().getMetadata("x-hitter"))
                    if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                        uuid = hitter.asString();
                if (uuid == null)
                {
                    e.getEntity().spigot().respawn();
                    e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
                    InventoryUtils.reItem(e.getEntity());
                    return;
                }
                killer = Bukkit.getPlayer(UUID.fromString(uuid));
            }
            else
            {
                e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
                e.getEntity().spigot().respawn();
                InventoryUtils.reItem(e.getEntity());
                return;
            }
        }

        if (killer != null)
        {
            Player finalKiller = killer;
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    PlayerInfo info = PlayerModify.getInfo(e.getEntity());
                    int nm = 28;

                    if (info != null)
                        nm = nm * (info.prestige * 110 / 100);

                    PlayerModify.addExp(finalKiller, nm);
                    e.getEntity().sendMessage(ChatColor.GREEN +
                            ChatColor.BOLD.toString() +
                            "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                            e.getEntity().getDisplayName() +
                            ChatColor.AQUA + " +" + nm + "XP"
                    );
                }
            }.runTaskAsynchronously(AreaPvP.getPlugin());
            e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!" + ChatColor.RESET + ChatColor.GRAY + " by " + e.getEntity().getDisplayName());

        }
        else
            e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        e.getEntity().spigot().respawn();
        InventoryUtils.reItem(e.getEntity());

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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;

        Player damager = (Player)  e.getEntity();
        Player hitter = (Player) e.getDamager();

        hitter.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitter", new FixedMetadataValue(AreaPvP.getPlugin(), hitter.getUniqueId().toString()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        player.teleport(player.getWorld().getSpawnLocation());
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (PlayerModify.isCreated(player))
                {
                    PlayerInfo info = PlayerModify.getInfo(player);
                    if (info == null)
                    {
                        PlayerModify.createBalance(player, true);
                        return;
                    }
                    player.setDisplayName(PlayerInfo.getPrefix(info.level, info.prestige) + player.getDisplayName());
                    return;
                }
                PlayerModify.createBalance(player, true);
            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());
        InventoryUtils.reItem(player);
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
