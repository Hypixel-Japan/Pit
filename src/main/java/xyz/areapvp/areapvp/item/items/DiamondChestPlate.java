package xyz.areapvp.areapvp.item.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.IShopItem;

public class DiamondChestPlate implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(Items.removeAttribute(new ItemStack(Material.DIAMOND_CHESTPLATE)), "type", getName());
    }

    @Override
    public String getName()
    {
        return "diamondChestPlate";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 500;
    }
}
