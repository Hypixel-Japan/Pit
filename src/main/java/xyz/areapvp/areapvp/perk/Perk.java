package xyz.areapvp.areapvp.perk;

import org.bukkit.entity.Player;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

public class Perk
{
    public static void update(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;
        setPerk(player, info.perk.toArray(new String[0]));
    }

    public static void setPerk(Player player, String... perk)
    {
        if (perk.length < 4)
            return;

        PlayerModify.removeMetaData(player, "perk1");
        PlayerModify.removeMetaData(player, "perk2");
        PlayerModify.removeMetaData(player, "perk3");
        PlayerModify.removeMetaData(player, "perk4");

        PlayerModify.setMetaData(player, "perk1", perk[0]);
        PlayerModify.setMetaData(player, "perk2", perk[1]);
        PlayerModify.setMetaData(player, "perk3", perk[2]);
        if (perk.length > 4)
            PlayerModify.setMetaData(player, "perk4", perk[3]);
    }
}
