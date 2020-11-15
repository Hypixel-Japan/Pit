package xyz.areapvp.areapvp.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.areapvp.areapvp.Items;

import java.util.Collections;

public class ShopItem
{
    public static ItemStack getItem(ItemStack itemStack, int gold, int needGold, int prestige, int needPrestige)
    {
        ItemStack stack = itemStack.clone();
        if (prestige < needPrestige)
        {
            stack.setType(Material.BEDROCK);
            stack.setItemMeta(error(stack, ChatColor.RED + "購入に必要なPrestigeが足りません！"));
            return stack;
        }

        if (gold < needGold)
        {
            stack.setType(Material.BEDROCK);
            stack.setItemMeta(error(stack, ChatColor.RED + "購入に必要なGoldが足りません！"));
            return stack;
        }

        stack = Items.quickLore(stack, ChatColor.GREEN + "クリックして購入！");
        return stack;
    }

    public static ItemMeta error(ItemStack stack, String why)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Collections.singletonList(why));
        return meta;
    }
}
