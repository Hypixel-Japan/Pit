package xyz.areapvp.areapvp;

import org.bukkit.entity.*;

public class Debugger
{
    public static void debug(Player player, String n)
    {
        if (!AreaPvP.debugging)
            return;
        player.sendMessage(n);
    }
}
