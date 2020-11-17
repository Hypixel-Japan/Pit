package xyz.areapvp.areapvp.item.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.IShopItem;

public class DiamondSword implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(Items.changeDamage(new ItemStack(Material.DIAMOND_SWORD), 7), "type", getName());
    }

    @Override
    public String getName()
    {
        return "diamondSword";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 150;
    }
}
