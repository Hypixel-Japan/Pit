package xyz.areapvp.areapvp.item.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.IShopItem;

public class DiamondBoots implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(Items.removeAttribute(new ItemStack(Material.DIAMOND_BOOTS)), "type", getName());
    }

    @Override
    public String getName()
    {
        return "diamondBoots";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 300;
    }
}
