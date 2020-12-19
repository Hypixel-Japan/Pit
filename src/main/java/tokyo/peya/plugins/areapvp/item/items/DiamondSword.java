package tokyo.peya.plugins.areapvp.item.items;

import org.bukkit.*;
import org.bukkit.inventory.*;
import tokyo.peya.plugins.areapvp.Items;
import tokyo.peya.plugins.areapvp.item.*;

public class DiamondSword implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        return Items.addMetaData(Items.removeAttribute(Items.changeDamage(Items.setUnbreakable(new ItemStack(Material.DIAMOND_SWORD)), 7)), "type", getName());
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
