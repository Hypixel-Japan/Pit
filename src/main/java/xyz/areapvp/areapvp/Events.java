package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import sun.awt.geom.AreaOp;
import xyz.areapvp.areapvp.level.Exp;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.math.BigDecimal;
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
            if (killer.getUniqueId() == e.getEntity().getUniqueId())
            {
                e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
                e.getEntity().spigot().respawn();
                InventoryUtils.reItem(e.getEntity());
            }

            Player finalKiller = killer;

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    PlayerInfo info = PlayerModify.getInfo(e.getEntity());

                    if (info == null)
                        return;

                    long nm = Exp.calcKillExp(finalKiller, e.getEntity(), info.level, info.prestige);

                    nm = (int) (nm * ((info.prestige == 0 ? 1: info.prestige * 0.5)));

                    double gse = 11;

                    gse = gse * ((info.prestige == 0 ? 1: info.prestige) * 110d / 100d);
                    BigDecimal bd = new BigDecimal(gse);

                    bd = bd.setScale(3, BigDecimal.ROUND_DOWN);

                    gse = bd.doubleValue();

                    AreaPvP.economy.depositPlayer(finalKiller, gse);
                    PlayerModify.addExp(finalKiller, nm);

                    PlayerInfo fs = PlayerModify.getInfo(e.getEntity());
                    String name = ChatColor.GRAY + "[1] " + e.getEntity().getDisplayName();
                    if (fs != null)
                        name = PlayerInfo.getPrefix(fs.level, fs.prestige) + ChatColor.GRAY + " " + e.getEntity().getDisplayName();

                    finalKiller.sendMessage(ChatColor.GREEN +
                            ChatColor.BOLD.toString() +
                            "KILL! " + ChatColor.RESET + ChatColor.GRAY + "on " +
                            name +
                            ChatColor.AQUA + " +" + nm + "XP " +
                            ChatColor.GOLD + " +" + gse + "g"
                    );
                    KillStreak.kill(finalKiller);
                    e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!" + ChatColor.RESET + ChatColor.GRAY + " by " +
                            PlayerInfo.getPrefix(info.level, info.prestige) + " " + ChatColor.GRAY + finalKiller.getName());
                }
            }.runTaskAsynchronously(AreaPvP.getPlugin());

        }
        else
            e.getEntity().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "DEATH!");
        e.getEntity().spigot().respawn();
        InventoryUtils.reItem(e.getEntity());

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        if (e.getTo().getY() > AreaPvP.config.getInt("spawnLoc"))
            e.getPlayer().setHealth(20);
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
    public void onArrow(ProjectileLaunchEvent e)
    {
        if (e.getEntity().getType() != EntityType.ARROW)
            return;
        Arrow arrow = (Arrow) e.getEntity();
        if (!(arrow.getShooter() instanceof  Player))
            return;
        Player launcher = (Player) arrow.getShooter();

        if (launcher.getLocation().getY() >= AreaPvP.config.getInt("spawnLoc"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e)
    {
        if (e.getDamager().getType() != EntityType.ARROW)
            return;

        if (!(e.getEntity() instanceof Player))
            return;

        if (e.getEntity().getLocation().getY() >= AreaPvP.config.getInt("spawnLoc"))
        {
            e.setCancelled(true);
            e.getDamager().remove();
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile))
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
        player.setFoodLevel(19);
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

        if (e.getBlockPlaced().getLocation().getY() >= AreaPvP.config.getInt("spawnLoc"))
        {
            e.setCancelled(true);
            return;
        }

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

    @EventHandler
    private void onHungr(FoodLevelChangeEvent e)
    {
        e.setFoodLevel(19);
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e)
    {
        AreaPvP.gui.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e)
    {
        AreaPvP.gui.remove(e.getPlayer().getUniqueId());
    }


    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncChat(AsyncPlayerChatEvent e)
    {
        PlayerInfo info = PlayerModify.getInfo(e.getPlayer());
        if (info == null)
            return;

        String prefix = PlayerInfo.getPrefixFull(info.level, info.prestige);

        e.setCancelled(true);

        String full = prefix + ChatColor.GRAY + " " + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage();

        Bukkit.getLogger().info(full);

        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> player.sendMessage(full));
    }
}
