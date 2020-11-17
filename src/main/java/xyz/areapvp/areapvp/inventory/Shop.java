package xyz.areapvp.areapvp.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.item.IShopItem;
import xyz.areapvp.areapvp.item.Items;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;
import xyz.areapvp.areapvp.perk.IPerkEntry;
import xyz.areapvp.areapvp.perk.Perks;

public class Shop
{
    public static void openInventory(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(Items.items.size() / 9.0) * 9, ChatColor.BLUE + "Item Shop");

        for (IShopItem item: Items.items)
        {
            if (item.getName().equals("blank"))
                inventory.addItem(item.getItem());
            else
                inventory.addItem(ShopItem.getItem(item.getItem(), balance, item.getNeedGold(), info.prestige, item.getNeedPrestige()));

        }
        player.openInventory(inventory);
    }

    public static void openPerkInventory(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, (Perks.perks.size() % 9 == 0 ? 1: Perks.perks.size() % 9) * 9, ChatColor.BLUE + "Perk Shop");
        for (IPerkEntry item: Perks.perks)
            inventory.addItem(ShopItem.getItem(item.getItem(), balance, item.getNeedGold(), info.prestige, item.getNeedPrestige()));
        player.openInventory(inventory);
    }
}
