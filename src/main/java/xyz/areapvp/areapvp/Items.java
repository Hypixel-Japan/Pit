package xyz.areapvp.areapvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Items
{
    public static final String keptOnDeath = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Kept on death";
    public static final String perkItem = ChatColor.GRAY + ChatColor.ITALIC.toString() + "Perk item";
    public static final String specialItem = ChatColor.YELLOW + "Special item";

    public static ItemStack setDisplayName(ItemStack b, String name)
    {
        ItemStack copy = b.clone();
        ItemMeta meta = copy.getItemMeta();
        meta.setDisplayName(name);
        copy.setItemMeta(meta);
        return copy;
    }

    public static ItemStack removeAttribute(ItemStack b)
    {
        ItemStack copy = b.clone();
        ItemMeta meta = copy.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        copy.setItemMeta(meta);
        return b;
    }

    public static ItemStack quickLore(ItemStack b, String t)
    {
        ItemMeta meta = b.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(t);
        lore.add("");
        if (b.getType() == Material.AIR)
            return b;
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

    public static ItemStack removeMetadata(ItemStack stack, String name)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        tagCompound.remove(name);
        nmStack.setTag(tagCompound);
        return CraftItemStack.asCraftMirror(nmStack);
    }

    public static boolean hasMetadata(ItemStack stack, String name)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        return tagCompound.getString(name) != null && !tagCompound.getString(name).equals("");
    }

    public static String getMetadata(ItemStack stack, String name)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        return tagCompound.getString(name);
    }

    public static HashMap<String, String> getMetadataList(ItemStack stack)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();

        try
        {
            Field field = NBTTagCompound.class.getDeclaredField("map");
            field.setAccessible(true);
            HashMap<String, NBTBase> map = (HashMap<String, NBTBase>) field.get(tagCompound);

            HashMap<String, String> result = new HashMap<>();
            map.forEach((s, nbtBase) -> {
                try
                {
                    new Gson().fromJson(nbtBase.toString(), Object.class);
                    result.put(s, new GsonBuilder().serializeNulls().setPrettyPrinting().create()
                            .toJson(new Gson().fromJson(nbtBase.toString(), Object.class)));
                }
                catch (Exception ignored)
                {
                    result.put(s, nbtBase.toString());
                }
            });
            return result;
        }
        catch (Exception e)
        {
            return new HashMap<>();
        }

    }


    public static ItemStack addMetaData(ItemStack stack, String key, String value)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmStack.getTag() != null ? nmStack.getTag(): new NBTTagCompound();
        tagCompound.setString(key, value);
        nmStack.setTag(tagCompound);
        return CraftItemStack.asCraftMirror(nmStack);
    }

    public static ItemStack noDrop(ItemStack b)
    {
        return Items.addMetaData(b, "noDrop", "1b");
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

    public static ItemStack changeDamage(ItemStack stack, double damage)
    {
        net.minecraft.server.v1_12_R1.ItemStack craftItem = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound compound = craftItem.hasTag() ? craftItem.getTag(): new NBTTagCompound();
        if (compound == null)
            compound = new NBTTagCompound();
        NBTTagList mod = new NBTTagList();
        NBTTagCompound dm = new NBTTagCompound();
        dm.set("AttributeName", new NBTTagString("generic.attackDamage"));
        dm.set("Name", new NBTTagString("genericd.attackDamage"));
        dm.set("Amount", new NBTTagDouble(damage));
        dm.set("Operation", new NBTTagInt(0));
        dm.set("UUIDLeast", new NBTTagInt(UUID.randomUUID().hashCode()));
        dm.set("UUIDMost", new NBTTagInt(UUID.randomUUID().hashCode()));
        dm.set("Slot", new NBTTagString("mainhand"));
        mod.add(dm);
        compound.set("AttributeModifiers", mod);
        craftItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(craftItem);
    }

    public static ItemStack addGlow(ItemStack target)
    {
        if (target.getType() == Material.AIR)
            return target;
        ItemStack stack = target.clone();
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }
}
