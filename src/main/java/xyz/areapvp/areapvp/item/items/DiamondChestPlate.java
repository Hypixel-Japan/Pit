package xyz.areapvp.areapvp.item.items;

import org.bukkit.*;
import org.bukkit.inventory.*;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.*;

public class DiamondChestPlate implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(Items.removeAttribute(Items.setUnbreakable(new ItemStack(Material.DIAMOND_CHESTPLATE))), "type", getName());
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
