package xyz.areapvp.areapvp.player;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class PlayerInfo
{
    public final int level;
    public final long exp;
    public final int prestige;
    public final ArrayList<String> perk;
    public final ArrayList<String> ownPerk;

    public PlayerInfo(int level, long exp, int prestige, ArrayList<String> perk, ArrayList<String> ownPerk)
    {
        this.level = level;
        this.exp = exp;
        this.prestige = prestige;
        this.perk = perk;
        this.ownPerk = ownPerk;
    }

    /**
     * 参考: http://lovedvoraklayout.hatenablog.com/entry/roman-numerals-to-arabic
     */
    public static String arabicToRoman(int n)
    {
        if (n <= 0)
            return "";
        else if (n >= 3999)
            return "IN-";
        int[] number = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] roma = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < 13; i++)
        {
            int ii = n / number[i];
            for (int j = 0; j < ii; j++)
                ans.append(roma[i]);

            n = n % number[i];
        }
        return ans.toString();
    }

    public static String getPrefixFull(int level, int prestige)
    {
        String bracketColor = getBracketColor(prestige).toString();
        String levelColor = getLevelColor(level);
        String a = "";
        if (prestige != 0)
            a = ChatColor.YELLOW + arabicToRoman(prestige) + bracketColor + "-";
        return bracketColor + "[" + a + levelColor + level + bracketColor + "]";
    }

    public static String getPrefix(int level, int prestige)
    {
        String bracketColor = getBracketColor(prestige).toString();
        String levelColor = getLevelColor(level);
        return bracketColor + "[" + levelColor + level + bracketColor + "]";
    }

    private static ChatColor getBracketColor(int prestige)
    {
        if (prestige == 0)
            return ChatColor.GRAY;
        else if (prestige < 5)
            return ChatColor.BLUE;
        else if (prestige < 10)
            return ChatColor.YELLOW;
        else if (prestige < 15)
            return ChatColor.GOLD;
        else if (prestige < 20)
            return ChatColor.RED;
        else if (prestige < 25)
            return ChatColor.DARK_PURPLE;
        else if (prestige < 30)
            return ChatColor.LIGHT_PURPLE;
        else if (prestige < 40)
            return ChatColor.WHITE;
        else
            return ChatColor.AQUA;
    }

    private static String getLevelColor(int level)
    {
        ChatColor c;
        if (level < 10)
            c = ChatColor.GRAY;
        else if (level < 20)
            c = ChatColor.BLUE;
        else if (level < 30)
            c = ChatColor.DARK_AQUA;
        else if (level < 40)
            c = ChatColor.DARK_GREEN;
        else if (level < 50)
            c = ChatColor.GREEN;
        else if (level < 60)
            c = ChatColor.YELLOW;
        else if (level < 70)
            return ChatColor.GOLD + ChatColor.BOLD.toString();
        else if (level < 80)
            return ChatColor.RED + ChatColor.BOLD.toString();
        else if (level < 90)
            return ChatColor.DARK_RED + ChatColor.BOLD.toString();
        else if (level < 100)
            return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString();
        else if (level < 110)
            return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString();
        else if (level < 120)
            return ChatColor.WHITE + ChatColor.BOLD.toString();
        else if (level == 120)
            return ChatColor.AQUA + ChatColor.BOLD.toString();
        else
            c = ChatColor.GRAY;
        return c.toString();

    }

    public static ItemStack getHead(String playerName)
    {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(playerName);
        stack.setItemMeta(meta);
        return stack;
    }

}
