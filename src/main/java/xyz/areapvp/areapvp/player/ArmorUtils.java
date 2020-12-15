package xyz.areapvp.areapvp.player;

import org.bukkit.inventory.*;

public class ArmorUtils
{
    public static MaterialType getMaterialType(ItemStack stack)
    {
        if (stack == null)
            return MaterialType.UNKNOWN;
        switch (stack.getType())
        {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return MaterialType.LEATHER;
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return MaterialType.CHAIN;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return MaterialType.IRON;
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return MaterialType.DIAMOND;
            default:
                return MaterialType.UNKNOWN;
        }
    }

    public static ArmorType getType(ItemStack stack)
    {
        if (stack == null)
            return ArmorType.UNKNOWN;
        switch (stack.getType())
        {
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
                return ArmorType.HELMET;
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return ArmorType.CHESTPLATE;
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
                return ArmorType.LEGGINGS;
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                return ArmorType.BOOTS;
            default:
                return ArmorType.UNKNOWN;
        }
    }

    public static boolean hasStrong(ItemStack stack, ItemStack stack2)
    {
        if (stack == null)
            return true;
        else if (stack2 == null)
            return false;
        return getStrong(stack) < getStrong(stack2);
    }

    public static boolean hasArmor(ItemStack stack)
    {
        if (stack == null)
            return false;
        switch (stack.getType())
        {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
        }
        return false;
    }

    public static int getStrong(ItemStack stack)
    {
        if (stack == null)
            return -1;
        switch (stack.getType())
        {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return 0;
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return 1;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return 2;
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 3;
            default:
                return -1;
        }
    }

    public enum ArmorType
    {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        UNKNOWN
    }

    public enum MaterialType
    {
        DIAMOND,
        IRON,
        GOLD,
        LEATHER,
        CHAIN,
        UNKNOWN
    }
}
