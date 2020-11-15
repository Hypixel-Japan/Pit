package xyz.areapvp.areapvp;

import com.sun.javafx.sg.prism.web.NGWebView;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.inventory.Shop;
import xyz.areapvp.areapvp.item.IShopItem;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.awt.geom.Area;
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

                    int nm = 28;

                    if (info != null)
                        nm = nm * ((info.prestige == 0 ? 1: info.prestige) * 110 / 100);

                    int gse = 11;

                    if (info != null)
                        gse = gse * ((info.prestige == 0 ? 1: info.prestige) * 110 / 100);

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
                            ChatColor.GOLD + " +" + gse + ".00g"
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
                    PlayerEditor.changePlayerHead(player, info.prestige, info.level, PlayerEditor.Type.CREATE);

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

    @EventHandler
    private void onPickUp(InventoryClickEvent e)
    {
        Player player = (Player) e.getInventory().getViewers().get(0);
        if (player == null)
            return;
        String type = AreaPvP.gui.get(player.getUniqueId());
        if (type == null)
            return;
        e.setCancelled(true);

        if (e.getInventory() instanceof PlayerInventory)
            return;

        ItemStack item = e.getCurrentItem();

        if (item.getType() == Material.BEDROCK)
        {
            player.sendMessage(ChatColor.RED + "あなたはこれを購入することができません！");
            return;
        }

        IShopItem shopItem = xyz.areapvp.areapvp.item.Items.getItem(Items.getMetaData(item, "type"));

        if (shopItem == null)
        {
            player.sendMessage(ChatColor.RED + "アイテムが不明です！");
            return;
        }

        if (AreaPvP.economy.getBalance(player) >= shopItem.getNeedGold())
        {
            player.getInventory().addItem(shopItem.getItem());
            player.sendMessage(ChatColor.GREEN + "アイテムを購入しました！");
            AreaPvP.economy.withdrawPlayer(player, shopItem.getNeedGold());
            player.closeInventory();
        }
    }

    @EventHandler
    private void onEntityRight(PlayerInteractEntityEvent e)
    {
        if (e.getRightClicked().getType() != EntityType.VILLAGER)
            return;

        if (e.getPlayer().hasPermission("areapvp.admin"))
        {
            if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
            {
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Shop Creator 3000"))
                {
                    e.getRightClicked().setMetadata("areaPvP", new FixedMetadataValue(AreaPvP.getPlugin(), "item"));
                    e.getPlayer().sendMessage(ChatColor.GREEN + "アイテムショップを作成しました。");
                    return;
                }
            }
        }

        if (!e.getRightClicked().hasMetadata("areaPvP"))
            return;
        String type = null;
        for (MetadataValue value: e.getRightClicked().getMetadata("areaPvP"))
            if (value.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                type = value.asString();
        if (type == null)
            return;
        e.setCancelled(true);
        switch (type)
        {
            case "item":
                AreaPvP.gui.put(e.getPlayer().getUniqueId(), "item");
                Shop.openInventory(e.getPlayer());
                break;
        }
    }
}
