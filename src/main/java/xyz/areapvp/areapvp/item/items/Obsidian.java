package xyz.areapvp.areapvp.item.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.IShopItem;

public class Obsidian implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(new ItemStack(Material.OBSIDIAN, 8), "type", getName());
    }

    @Override
    public String getName()
    {
        return "obsidian";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 50;
    }
}
