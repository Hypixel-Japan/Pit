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

    public static PlayerInfo getInfo(Player player)
    {
        return new PlayerInfo(getLevel(player),
                getExp(player),
                getPrestige(player),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
