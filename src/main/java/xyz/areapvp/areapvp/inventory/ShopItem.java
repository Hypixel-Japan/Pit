package xyz.areapvp.areapvp.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.areapvp.areapvp.Items;

import java.util.Collections;
import java.util.UUID;

public class ShopItem
{
    public static ItemStack getItem(ItemStack itemStack, int gold, int needGold, int prestige, int needPrestige)
    {
        ItemStack stack = itemStack.clone();
        if (prestige < needPrestige)
        {
            stack = new ItemStack(Material.BEDROCK);
            stack.setItemMeta(error(ChatColor.RED + "Unknown", stack,ChatColor.RED + "購入に必要なPrestigeが足りません！"));
            stack = Items.addMetaData(stack, "r", UUID.randomUUID().toString());
            stack = Items.addMetaData(stack, "notBuyable", "1b");
            return stack;
        }

        if (gold < needGold)
        {
            stack.setItemMeta(error(stack.getItemMeta().getDisplayName(),stack, ChatColor.RED + "購入に必要なGoldが足りません！"));
            stack = Items.addMetaData(stack, "notBuyable", "1b");
            stack = Items.addMetaData(stack, "r", UUID.randomUUID().toString());
            return stack;
        }

        stack = Items.quickLore(stack, ChatColor.GOLD + "金額：" + needGold + "g");
        stack = Items.quickLore(stack, ChatColor.GREEN + "クリックして購入！");

        return stack;
    }

    public static ItemMeta error(String title,  ItemStack stack, String why)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(Collections.singletonList(why));
        return meta;
    }
}
