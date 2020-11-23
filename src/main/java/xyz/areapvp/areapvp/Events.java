package xyz.areapvp.areapvp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;
import xyz.areapvp.areapvp.perk.Perk;

import java.math.BigDecimal;
import java.util.stream.IntStream;

public class Events implements Listener
{
    private static String getDamageIndicator(double damage, Player damager)
    {
        StringBuilder base = new StringBuilder();

        BigDecimal damD = new BigDecimal(String.valueOf(damage))
                .divide(new BigDecimal(2), BigDecimal.ROUND_DOWN)
                .setScale(0, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal hpD = new BigDecimal(String.valueOf(damager.getHealth()))
                .divide(new BigDecimal(2), BigDecimal.ROUND_DOWN)
                .setScale(0, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal maxD = new BigDecimal(String.valueOf(damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
                .divide(new BigDecimal(2), BigDecimal.ROUND_DOWN)
                .setScale(0, BigDecimal.ROUND_HALF_DOWN);

        if (hpD.compareTo(new BigDecimal(0)) < 1.0 || hpD.compareTo(damD) <= 0)
        {
            base.append(ChatColor.RED).append("❤");
            IntStream.range(0, maxD.subtract(new BigDecimal(1)).intValue())
                    .forEach(i -> base.append(ChatColor.BLACK).append("❤"));
            return base.toString();
        }


        IntStream.range(0, hpD.subtract(damD).intValue())
                .forEach(i -> base.append(ChatColor.DARK_RED).append("❤"));
        IntStream.range(0, damD.intValue())
                .forEach(i -> base.append(ChatColor.RED).append("❤"));
        IntStream.range(0, maxD.subtract(hpD).intValue())
                .forEach(i -> base.append(ChatColor.BLACK).append("❤"));
        return base.toString();
    }

    private static boolean isCrit(Player p)
    {
        return !p.isOnGround() && p.getFallDistance() > 0 &&
                !p.getLocation().getBlock().isLiquid() &&
                !p.isInsideVehicle() && !p.isSprinting() &&
                !p.hasPotionEffect(PotionEffectType.BLINDNESS);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
        Player killer = e.getEntity().getKiller();
        Kill.processKill(killer, e.getEntity());
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
        if (!(arrow.getShooter() instanceof Player))
            return;
        Player launcher = (Player) arrow.getShooter();

        if (launcher.getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            return;
        }

        AreaPvP.arrows.put(arrow.getUniqueId(), 26);
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e)
    {
        if (e.getDamager().getType() != EntityType.ARROW || e.getDamager().getType() == EntityType.FISHING_HOOK)
            return;

        if (!(e.getEntity() instanceof Player))
            return;

        if (e.getEntity().getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            e.getDamager().remove();
        }

        Arrow arrow = (Arrow) e.getEntity();

        if (!(arrow.getShooter() instanceof Player))
            return;

        ((Player) arrow.getShooter()).setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        e.getEntity().setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        e.getEntity().setMetadata("x-hitter", new FixedMetadataValue(AreaPvP.getPlugin(), ((Player) arrow.getShooter()).getUniqueId()));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;

        if (e.getEntity().getLocation().getY() >= AreaPvP.spawnloc || e.getDamager().getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            return;
        }

        Player damager = (Player) e.getEntity();
        Player hitter = (Player) e.getDamager();

        hitter.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        damager.setMetadata("x-hitter", new FixedMetadataValue(AreaPvP.getPlugin(), hitter.getUniqueId().toString()));

        System.out.println(e.getDamage());
        hitter.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder(ChatColor.GRAY + damager.getName() + " "
                        + getDamageIndicator(e.getDamage(), damager)).create()
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        player.setFoodLevel(19);

        Perk.update(player);

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

        if (e.getBlockPlaced().getLocation().getY() >= AreaPvP.spawnloc)
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
        if (e.getInventory() instanceof PlayerInventory)
            return;
        System.out.println("Called");
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

    @EventHandler
    private void onCollision(VehicleEntityCollisionEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockForm(BlockFromToEvent e)
    {
        if (e.getBlock().getType() == Material.DRAGON_EGG)
            return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockFade(BlockFadeEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent e)
    {
        if (Items.hasMetadata(e.getItemDrop().getItemStack(), "noDrop"))
        {
            e.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "OOPS! " + ChatColor.RESET + ChatColor.RED + "このアイテムはドロップできません！");
            e.setCancelled(true);
        }
    }
}
