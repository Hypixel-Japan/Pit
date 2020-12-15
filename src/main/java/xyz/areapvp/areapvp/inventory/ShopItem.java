package xyz.areapvp.areapvp.inventory;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import xyz.areapvp.areapvp.*;

import java.util.*;

public class ShopItem
{
    public static ItemStack getItem(ItemStack itemStack, int gold, int needGold, int prestige, int needPrestige, int level, int needLevel)
    {
        ItemStack stack = itemStack.clone();

        if (level < needLevel)
        {
            stack = new ItemStack(Material.BEDROCK);
            stack.setItemMeta(error(ChatColor.RED + "Unknown", stack, ChatColor.RED + "購入に必要なLevelが足りません！"));
            stack = Items.addMetaData(stack, "r", UUID.randomUUID().toString());
            stack = Items.addMetaData(stack, "notBuyable", "1b");
            return stack;
        }
        return getItem(itemStack, gold, needGold, prestige, needPrestige);
    }

    public static ItemStack getItem(ItemStack itemStack, int gold, int needGold, int prestige, int needPrestige)
    {
        ItemStack stack = itemStack.clone();
        if (prestige < needPrestige)
        {
            stack = new ItemStack(Material.BEDROCK);
            stack.setItemMeta(error(ChatColor.RED + "Unknown", stack, ChatColor.RED + "購入に必要なPrestigeが足りません！"));
            stack = Items.addMetaData(stack, "r", UUID.randomUUID().toString());
            stack = Items.addMetaData(stack, "notBuyable", "1b");
            return stack;
        }

        if (gold < needGold)
        {
            stack.setItemMeta(error(stack.getItemMeta().getDisplayName(), stack, ChatColor.RED + "購入に必要なGoldが足りません！"));
            stack = Items.addMetaData(stack, "notBuyable", "1b");
            stack = Items.addMetaData(stack, "r", UUID.randomUUID().toString());
            return stack;
        }

        stack = Items.quickLore(stack, ChatColor.GOLD + "金額：" + needGold + "g");
        stack = Items.quickLore(stack, ChatColor.GREEN + "クリックして購入！");

        return stack;
    }

    public static ItemMeta error(String title, ItemStack stack, String why)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(Collections.singletonList(why));
        return meta;
    }
}
