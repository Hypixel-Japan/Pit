package xyz.areapvp.areapvp.level;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class PlayerInfo
{
    public final int level;
    public final long exp;
    public final int prestige;
    public final ArrayList<String> perk;

    public PlayerInfo(int level, long exp, int prestige, ArrayList<String> perk)
    {
        this.level = level;
        this.exp = exp;
        this.prestige = prestige;
        this.perk = perk;
    }

    public static String getPrefix(int level, int prestige)
    {
        String bracketColor = getBracketColor(prestige).toString();
        String levelColor = getLevelColor(level);
        return bracketColor + "[" + levelColor + level + bracketColor + "]" + ChatColor.WHITE;
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
            return ChatColor.WHITE.toString();
        else if (level  == 120)
            return ChatColor.AQUA + ChatColor.BOLD.toString();
        else
            c = ChatColor.GRAY;
        return c.toString();

    }
}
