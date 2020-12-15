package xyz.areapvp.areapvp.item.items;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.item.*;

import java.util.*;

public class ItemAir implements IShopItem
{
    @Override
    public ItemStack getItem()
    {
        ItemStack stack = Items.addMetaData(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8), "r", UUID.randomUUID().toString());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE.toString());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public String getName()
    {
        return "blank";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 0;
    }
}
