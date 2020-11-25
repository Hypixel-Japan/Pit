package xyz.areapvp.areapvp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.command.PitDebug;
import xyz.areapvp.areapvp.inventory.Shop;
import xyz.areapvp.areapvp.item.IShopItem;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;
import xyz.areapvp.areapvp.perk.IPerkEntry;
import xyz.areapvp.areapvp.perk.PerkInventory;
import xyz.areapvp.areapvp.perk.Perks;

public class GUI implements Listener
{

    @EventHandler
    private static void onEntityRight(PlayerInteractEntityEvent e)
    {
        if (e.getPlayer().isSneaking() && e.getRightClicked() instanceof Player &&
                e.getPlayer().getLocation().getY() >= AreaPvP.spawnloc)
            ProfileViewer.viewPlayer((Player) e.getRightClicked(), e.getPlayer());
        //下でReturnを想定


        if (e.getRightClicked().getType() != EntityType.VILLAGER)
            return;

        if (e.getPlayer().hasPermission("areapvp.admin"))
        {
            if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR &&
                    e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null)
            {
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Shop Creator 3000"))
                {
                    e.getRightClicked().addScoreboardTag("areaPvP::Item");
                    e.getPlayer().sendMessage(ChatColor.GREEN + "アイテムショップを作成しました。");
                    return;
                }
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "PerkShop Creator 3000"))
                {
                    e.getRightClicked().addScoreboardTag("areaPvP::Perk");
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Perkショップを作成しました。");
                    return;
                }
            }
        }

        String type = null;

        if (e.getRightClicked().getScoreboardTags().contains("areaPvP::Perk"))
            type = "perk";
        else if (e.getRightClicked().getScoreboardTags().contains("areaPvP::Item"))
            type = "item";
        else if (e.getRightClicked().getScoreboardTags().contains("areaPvP::Prestige"))
            type = "prestige";

        if (type == null)
            return;

        e.setCancelled(true);
        switch (type)
        {
            case "item":
                Shop.openInventory(e.getPlayer());
                AreaPvP.gui.put(e.getPlayer().getUniqueId(), "item");
                break;
            case "perk":
                AreaPvP.gui.put(e.getPlayer().getUniqueId(), "firstPerk");
                Shop.openPerkInventory(e.getPlayer());
                break;
        }
    }

    public static void playerPerkBuyProcess(Player player, IPerkEntry item)
    {
        if (item == null)
            return;

        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        if (info.perk.contains(item.getName()))
        {
            player.sendMessage(ChatColor.RED + "あなたはすでにこのPerkを使用しています。");
            player.closeInventory();
            return;
        }

        if (info.ownPerk.contains(item.getName()))
        {
            player.sendMessage(ChatColor.GREEN + "Perkを選択しました。");
            PlayerModify.addPerk(player, item.getName());
            item.onBuy(player);
            player.closeInventory();
            return;
        }

        if (info.level < item.getNeedLevel())
        {
            player.sendMessage(ChatColor.RED + "まだこのPerkを購入できません。");
            player.closeInventory();
            return;
        }

        if (AreaPvP.economy.getBalance(player) >= item.getNeedGold())
        {
            player.sendMessage(ChatColor.GREEN + "Perkを購入しました！");
            AreaPvP.economy.withdrawPlayer(player, item.getNeedGold());
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    PlayerModify.addOwnPerk(player, item.getName());
                    PlayerModify.addPerk(player, item.getName());
                }
            }.runTaskAsynchronously(AreaPvP.getPlugin());
            item.onBuy(player);
            player.closeInventory();
        }
    }

    public static void playerItemBuyProcess(Player player, IShopItem item)
    {

        if (item == null)
            return;

        if (AreaPvP.economy.getBalance(player) >= item.getNeedGold())
        {
            player.getInventory().addItem(item.getItem());
            player.sendMessage(ChatColor.GREEN + "アイテムを購入しました！");
            AreaPvP.economy.withdrawPlayer(player, item.getNeedGold());
            player.closeInventory();
        }
    }

    @EventHandler
    private void onPickUp(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        if (player == null)
            return;
        String type = AreaPvP.gui.get(player.getUniqueId());
        if (type == null)
            return;
        e.setCancelled(true);

        if (e.getInventory() instanceof PlayerInventory)
            return;

        ItemStack item = e.getCurrentItem();

        if (item == null)
            return;

        if (Items.hasMetadata(item, "notBuyable"))
        {
            player.sendMessage(ChatColor.RED + "あなたはこれを購入することができません！");
            return;
        }

        switch (type)
        {
            case "item":
                if (!e.getClickedInventory().getName().equals(ChatColor.BLUE + "Item Shop"))
                    return;
                IShopItem shopItem = xyz.areapvp.areapvp.item.Items.getItem(Items.getMetadata(item, "type"));
                playerItemBuyProcess(player, shopItem);
                break;
            case "perk":
                if (!e.getClickedInventory().getName().equals(ChatColor.BLUE + "Perk Shop"))
                    return;

                if (item.getType() == Material.AIR)
                    return;

                if (item.getType() == Material.BEDROCK)
                {
                    player.sendMessage(ChatColor.RED + "レベルが不足しています！");
                    player.closeInventory();
                    return;
                }

                if (item.getType() == Material.DIAMOND_BLOCK)
                {
                    String ty;
                    if ((ty = Items.getMetadata(item, "slot")) == null)
                        return;

                    PlayerInfo info = PlayerModify.getInfo(player);

                    if (info == null)
                        return;
                    Long k;
                    if ((k = PitDebug.parseLong(ty)) == null)
                        return;
                    int i = Math.toIntExact(k) - 1;

                    if (info.perk.size() > i)
                        PlayerModify.removePerk(player, info.perk.get(i));

                    player.closeInventory();
                    return;
                }  //使わない処理

                IPerkEntry entry = Perks.getPerk(Items.getMetadata(item, "type"));
                playerPerkBuyProcess(player, entry);
                break;
            case "firstPerk":
                if (!e.getClickedInventory().getName().equals(ChatColor.BLUE + "Perk Shop"))
                    return;

                if (item.getType() == Material.AIR)
                    return;

                if (item.getType() == Material.BEDROCK)
                {
                    player.sendMessage(ChatColor.RED + "レベルが不足しています！");
                    player.closeInventory();
                    return;
                }

                player.closeInventory();
                AreaPvP.gui.put(player.getUniqueId(), "perk");
                Long v;
                if ((v = PitDebug.parseLong(Items.getMetadata(item, "perkSlot"))) == null)
                    return;

                player.openInventory(PerkInventory.getPerksInventory(player, Math.toIntExact(v)));
                break;
            case "profile":
                ProfileViewer.onPickUp(player, item);
                break;
        }

    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e)
    {
        if (e.getEntity().getType() != EntityType.VILLAGER)
            return;
        String type = null;

        if (e.getEntity().getScoreboardTags().contains("areaPvP::Perk"))
            type = "perk";
        else if (e.getEntity().getScoreboardTags().contains("areaPvP::Item"))
            type = "item";
        else if (e.getEntity().getScoreboardTags().contains("areaPvP::Prestige"))
            type = "prestige";

        if (type == null)
            return;
        e.setCancelled(true);
    }

}
