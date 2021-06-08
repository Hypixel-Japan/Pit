package tokyo.peya.plugins.areapvp.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.*;
import org.bukkit.scheduler.*;
import tokyo.peya.plugins.areapvp.Items;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.perk.*;
import tokyo.peya.plugins.areapvp.play.InventoryUtils;
import tokyo.peya.plugins.areapvp.play.*;
import tokyo.peya.plugins.areapvp.play.decoration.*;
import tokyo.peya.plugins.areapvp.player.*;

import java.math.*;
import java.util.stream.*;

public class Events implements Listener
{
    public static String getDamageIndicator(double damage, Player damager)
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

    @EventHandler(ignoreCancelled = true)
    public void onKill(PlayerDeathEvent e)
    {
        Kill.processKill(e.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent e)
    {
        Entity ee = e.getEntity();
        if (!(ee instanceof Player))
            return;
        EntityDamageEvent.DamageCause type = e.getCause();
        if (type == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrow(ProjectileLaunchEvent e)
    {
        if (e.getEntity().getType() == EntityType.FISHING_HOOK)
            e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(1.15));

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

    @EventHandler(ignoreCancelled = true)
    public void onCaught(ProjectileHitEvent e)
    {
        if (!(e.getEntityType() == EntityType.FISHING_HOOK))
            return;
        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof Player))
            return;
        if (e.getHitEntity().getLocation().getY() >= AreaPvP.spawnloc)
            ((Player) e.getHitEntity()).setNoDamageTicks(100);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent e)
    {
        if (e.getDamager().getType() != EntityType.ARROW)
            return;

        if (!(e.getEntity() instanceof Player))
            return;


        if (e.getEntity().getLocation().getY() >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            e.getDamager().remove();
        }

        Arrow arrow = (Arrow) e.getDamager();

        if (!(arrow.getShooter() instanceof Player))
            return;

        ((Player) arrow.getShooter()).setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        Sounds.play((Player) arrow.getShooter(), Sounds.SoundType.ARROW_HIT);
        e.getEntity().setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), 15));
        e.getEntity().setMetadata("x-hitter", new FixedMetadataValue(AreaPvP.getPlugin(), ((Player) arrow.getShooter()).getUniqueId()));
        if (!((Player) arrow.getShooter()).hasMetadata("damageDebug"))
            ((Player) arrow.getShooter()).spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(ChatColor.GRAY + e.getEntity().getName() + " "
                            + getDamageIndicator(e.getDamage(), (Player) e.getEntity())).create()
            );
        else
            ((Player) arrow.getShooter()).spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(ChatColor.RED.toString() + e.getDamage() + " => " + ((Player) e.getEntity()).getHealth()).create()
            );
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setFoodLevel(19);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                InfoContainer.initialize(player);
                Perk.update(player);
                InventoryUtils.reItem(player);
            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());

    }

    @EventHandler(ignoreCancelled = true)
    private void onBreak(BlockBreakEvent e)
    {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (e.getBlock().hasMetadata("placed"))
        {
            AreaPvP.block.remove(e.getBlock().getLocation());
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent e)
    {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (e.getBlockPlaced().getLocation().getY() + 5 >= AreaPvP.spawnloc)
        {
            e.setCancelled(true);
            return;
        }

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
        AreaPvP.block.put(e.getBlock().getLocation(), new Tuple<>(remove, e.getBlockReplacedState().getType()));
    }

    @EventHandler(ignoreCancelled = true)
    private void onHungr(FoodLevelChangeEvent e)
    {
        e.setFoodLevel(19);
    }

    @EventHandler(ignoreCancelled = true)
    private void onLeave(PlayerQuitEvent e)
    {
        AreaPvP.gui.remove(e.getPlayer().getUniqueId());
        InfoContainer.unnick(e.getPlayer());
        AreaPvP.refreshScoreBoard(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    private void onClose(InventoryCloseEvent e)
    {
        if (e.getInventory() instanceof PlayerInventory)
            return;
        AreaPvP.gui.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onAsyncChat(AsyncPlayerChatEvent e)
    {
        if (!InfoContainer.isInitialize(e.getPlayer()))
            return;
        PlayerInfo info = InfoContainer.getInfoAllowNick(e.getPlayer());

        String prefix = PlayerInfo.getPrefixFull(info.level, info.prestige);

        e.setCancelled(true);

        String full = prefix + ChatColor.GRAY + " " + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage();

        Bukkit.getLogger().info(full);

        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> player.sendMessage(full));
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    private void onDrop(PlayerDropItemEvent e)
    {
        if (Items.hasMetadata(e.getItemDrop().getItemStack(), "noDrop"))
        {
            e.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "OOPS! " + ChatColor.RESET + ChatColor.RED + "このアイテムはドロップできません！");
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPickup(EntityPickupItemEvent e)
    {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        ItemStack stack = e.getItem().getItemStack();
        if (ArmorUtils.hasArmor(stack))
        {
            if (ArmorUtils.getMaterialType(stack) != ArmorUtils.MaterialType.DIAMOND &&
                    PerkProcess.countItem(player, stack.getType()) > 2)
            {
                e.setCancelled(true);
                return;
            }

            switch (ArmorUtils.getType(stack))
            {
                case HELMET:
                    if (ArmorUtils.hasStrong(player.getInventory().getHelmet(), stack))
                    {
                        ItemStack b = player.getInventory().getHelmet();
                        player.getInventory().setHelmet(stack);
                        if (b != null)
                            player.getInventory().addItem(b);
                    }
                    else
                        player.getInventory().addItem(stack);
                    break;
                case CHESTPLATE:
                    if (ArmorUtils.hasStrong(player.getInventory().getChestplate(), stack))
                    {
                        ItemStack b = player.getInventory().getChestplate();
                        player.getInventory().setChestplate(stack);
                        if (b != null)
                            player.getInventory().addItem(b);
                    }
                    else
                        player.getInventory().addItem(stack);
                    break;
                case LEGGINGS:
                    if (ArmorUtils.hasStrong(player.getInventory().getLeggings(), stack))
                    {
                        ItemStack b = player.getInventory().getLeggings();
                        player.getInventory().setLeggings(stack);
                        if (b != null)
                            player.getInventory().addItem(b);
                    }
                    else
                        player.getInventory().addItem(stack);
                    break;
                case BOOTS:
                    if (ArmorUtils.hasStrong(player.getInventory().getBoots(), stack))
                    {
                        ItemStack b = player.getInventory().getBoots();
                        player.getInventory().setBoots(stack);
                        if (b != null)
                            player.getInventory().addItem(b);
                    }
                    else
                        player.getInventory().addItem(stack);
                    break;
                case UNKNOWN:
            }
            e.getItem().remove();
            e.setCancelled(true);
        }
    }
}
