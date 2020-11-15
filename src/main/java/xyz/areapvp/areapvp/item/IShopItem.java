package xyz.areapvp.areapvp.item;

import org.bukkit.inventory.ItemStack;

public interface IShopItem
{
    ItemStack getItem();
    String getName();
    int getNeedPrestige();
    int getNeedGold();
}
