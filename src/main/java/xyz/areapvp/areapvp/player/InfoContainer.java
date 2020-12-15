package xyz.areapvp.areapvp.player;

import org.bukkit.entity.*;
import org.bukkit.metadata.*;

import java.util.*;

public class InfoContainer
{
    public static boolean isInitialize(Player player)
    {
        return get(player, "AreaPvp.Init").equals("1");
    }

    public static void initialize(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            PlayerModify.createBalance(player, true);
        if (info == null)
        {
            new IllegalAccessException("Player not found.").printStackTrace();
            return;
        }

        PlayerModify.setMetaData(player, "AreaPvP.Level", info.level);
        PlayerModify.setMetaData(player, "AreaPvP.Exp", info.exp);
        PlayerModify.setMetaData(player, "AreaPvP.Prestige", info.prestige);
        PlayerModify.setMetaData(player, "AreaPvp.Init", "1");
    }

    public static long getAsLong(Player player, String key)
    {
        String s = get(player, key);
        return Long.parseLong(s);
    }

    public static int getAsInt(Player player, String key)
    {
        String s = get(player, key);
        return Integer.parseInt(s);
    }

    public static String get(Player player, String key)
    {
        Optional<MetadataValue> p = PlayerModify.getMetaData(player, key);
        return p.map(MetadataValue::asString).orElse("0");
    }

    public static int getPrestige(Player player)
    {
        return getAsInt(player, "AreaPvP.Prestige");
    }

    public static int getLevel(Player player)
    {
        return getAsInt(player, "AreaPvP.Level");
    }

    public static long getExp(Player player)
    {
        return getAsLong(player, "AreaPvP.Exp");
    }

    public static void unnick(Player player)
    {
        PlayerModify.removeMetaData(player, "AreaPvP.Nick.Level");
        PlayerModify.removeMetaData(player, "AreaPvP.Nick.Exp");
        PlayerModify.removeMetaData(player, "AreaPvp.Nick.Enabled");
    }

    public static void nick(Player player)
    {
        PlayerModify.setMetaData(player, "AreaPvP.Nick.Level", new Random().nextInt(119) + 1);
        PlayerModify.setMetaData(player, "AreaPvP.Nick.Exp", 0);
        PlayerModify.setMetaData(player, "AreaPvp.Nick.Enabled", "1");
    }

    public static boolean isNicked(Player player)
    {
        return get(player, "AreaPvp.Nick.Enabled").equals("1");
    }

    public static PlayerInfo getInfoAllowNick(Player player)
    {
        if (!get(player, "AreaPvp.Nick.Enabled").equals("1"))
            return getInfo(player);
        return new PlayerInfo(
                getAsInt(player, "AreaPvP.Nick.Level"),
                getAsLong(player, "AreaPvP.Nick.Exp"),
                0,
                new ArrayList<>(),
                new ArrayList<>()
        );

    }

    public static PlayerInfo getInfo(Player player)
    {
        return new PlayerInfo(
                getLevel(player),
                getExp(player),
                getPrestige(player),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
