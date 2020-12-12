package xyz.areapvp.areapvp.perk;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import xyz.areapvp.areapvp.player.PlayerInfo;
import xyz.areapvp.areapvp.player.PlayerModify;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Perk
{
    public static void update(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        remove(info.perk, player);

        setPerk(player, info.perk.toArray(new String[0]));
    }

    private static void remove(ArrayList<String> perks, Player player)
    {
        Optional<MetadataValue> opt1 = PlayerModify.getMetaData(player, "perk1");
        Optional<MetadataValue> opt2 = PlayerModify.getMetaData(player, "perk2");
        Optional<MetadataValue> opt3 = PlayerModify.getMetaData(player, "perk3");
        Optional<MetadataValue> opt4 = PlayerModify.getMetaData(player, "perk4");

        if (opt1.isPresent() && !perks.contains(opt1.get().asString()))
            Objects.requireNonNull(Perks.getPerk(opt1.get().asString())).onRemove(player);
        if (opt2.isPresent() && !perks.contains(opt2.get().asString()))
            Objects.requireNonNull(Perks.getPerk(opt2.get().asString())).onRemove(player);
        if (opt3.isPresent() && !perks.contains(opt3.get().asString()))
            Objects.requireNonNull(Perks.getPerk(opt3.get().asString())).onRemove(player);
        if (opt4.isPresent() && !perks.contains(opt4.get().asString()))
            Objects.requireNonNull(Perks.getPerk(opt4.get().asString())).onRemove(player);
    }

    public static void setPerk(Player player, String... perk)
    {

        PlayerModify.removeMetaData(player, "perk1");
        PlayerModify.removeMetaData(player, "perk2");
        PlayerModify.removeMetaData(player, "perk3");
        PlayerModify.removeMetaData(player, "perk4");

        if (perk.length >= 1)
            PlayerModify.setMetaData(player, "perk1", perk[0]);
        if (perk.length >= 2)
            PlayerModify.setMetaData(player, "perk2", perk[1]);
        if (perk.length >= 3)
            PlayerModify.setMetaData(player, "perk3", perk[2]);
        if (perk.length >= 4)
            PlayerModify.setMetaData(player, "perk4", perk[3]);
    }

    public static boolean contains(Player player, String perk)
    {
        Optional<MetadataValue> perk1 = PlayerModify.getMetaData(player, "perk1");
        Optional<MetadataValue> perk2 = PlayerModify.getMetaData(player, "perk2");
        Optional<MetadataValue> perk3 = PlayerModify.getMetaData(player, "perk3");
        Optional<MetadataValue> perk4 = PlayerModify.getMetaData(player, "perk4");

        if (perk1.isPresent() && perk1.get().asString().equals(perk))
            return true;
        else if (perk2.isPresent() && perk2.get().asString().equals(perk))
            return true;
        else if (perk3.isPresent() && perk3.get().asString().equals(perk))
            return true;
        else return perk4.isPresent() && perk4.get().asString().equals(perk);
    }
}
