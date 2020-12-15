package xyz.areapvp.areapvp;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.*;
import xyz.areapvp.areapvp.command.*;
import xyz.areapvp.areapvp.inventory.*;
import xyz.areapvp.areapvp.item.*;
import xyz.areapvp.areapvp.perk.*;
import xyz.areapvp.areapvp.player.*;

public class GUI implements Listener
{

    @EventHandler
    private static void onEntityRight(PlayerInteractEntityEvent e)
    {
        if (e.getPlayer().isSneaking() && e.getRightClicked() instanceof Player &&
                e.getPlayer().getLocation().getY() >= AreaPvP.spawnloc)
        {
            if (!InfoContainer.isInitialize(e.getPlayer()))
                return;
            PlayerInfo info = InfoContainer.getInfo(e.getPlayer());
            if (info.prestige == 0 && info.level < 70)
                return;
            ProfileViewer.viewPlayer((Player) e.getRightClicked(), e.getPlayer());

        }
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
                else if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "PerkShop Creator 3000"))
                {
                    e.getRightClicked().addScoreboardTag("areaPvP::Perk");
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Perkショップを作成しました。");
                    return;
                }
                else if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Prestige Creator 3000™"))
                {
                    e.getRightClicked().addScoreboardTag("areaPvP::Prestige");
                    e.getPlayer().sendMessage(ChatColor.GREEN + "PrestigeNPCを作成しました。");
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
            case "prestige":
                Prestige.openInventry(e.getPlayer());
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
                if (item.getType() != Material.DIAMOND_BLOCK)
                {
                    IPerkEntry entry = Perks.getPerk(Items.getMetadata(item, "type"));
                    playerPerkBuyProcess(player, entry);
                }
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
            case "prestige":
                Prestige.onPickUp(player, item);
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
