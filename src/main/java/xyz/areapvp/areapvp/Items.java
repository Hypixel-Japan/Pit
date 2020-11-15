package xyz.areapvp.areapvp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items
{
    public static final String keptOnDeath = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Kept on death";
    public static final String perkItem = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Perk item";
    public static final String specialItem = ChatColor.YELLOW + "Special item";

    public static ItemStack setUnbreakable(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemMeta meta = b.getItemMeta();
        meta.setUnbreakable(true);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }


}
