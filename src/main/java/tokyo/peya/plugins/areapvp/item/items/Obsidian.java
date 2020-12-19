package tokyo.peya.plugins.areapvp.item.items;

import org.bukkit.*;
import org.bukkit.inventory.*;
import tokyo.peya.plugins.areapvp.Items;
import tokyo.peya.plugins.areapvp.item.*;

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
