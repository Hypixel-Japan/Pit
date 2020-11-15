package xyz.areapvp.areapvp;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Stack;

public class Items
{
    public static final String keptOnDeath = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Kept on death";
    public static final String perkItem = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Perk item";
    public static final String specialItem = ChatColor.YELLOW + "Special item";

    public static ItemStack quickLore(ItemStack b, String t)
    {
        ItemMeta meta = b.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(t);
        lore.add("");
        if (meta.hasLore())
            lore.addAll(meta.getLore());
        meta.setLore(lore);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack setPerk(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemStack stack = addMetaData(b, "perk", "perk");

        return quickLore(stack, perkItem);
    }


    public static ItemStack setSpecial(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemStack stack = addMetaData(b, "special", "special");

        return quickLore(stack, specialItem);
    }


    public static ItemStack setKeptOnDeath(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemStack stack = addMetaData(b, "keptOnDeath", "keptOnDeath");

        return quickLore(stack, keptOnDeath);
    }

    private static ItemStack removeMetadata(ItemStack stack, String name)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        tagCompound.remove(name);
        nmStack.setTag(tagCompound);
        return CraftItemStack.asCraftMirror(nmStack);
    }

    public static boolean hasMetadata(ItemStack stack,  String name)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        return tagCompound.getString(name) != null;
    }

    private static ItemStack addMetaData(ItemStack stack, String key, String value)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        tagCompound.setString(key, value);
        nmStack.setTag(tagCompound);
        return CraftItemStack.asCraftMirror(nmStack);
    }

    public static ItemStack setUnbreakable(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemMeta meta = b.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }


}
