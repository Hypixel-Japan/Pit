package xyz.areapvp.areapvp.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.item.IShopItem;
import xyz.areapvp.areapvp.item.Items;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

public class Shop
{
    public static void openInventory(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, 9);
        for (IShopItem item: Items.items)
            inventory.addItem(ShopItem.getItem(item.getItem(), balance, item.getNeedGold(), info.prestige, item.getNeedPrestige()));
        player.openInventory(inventory);
    }
}
