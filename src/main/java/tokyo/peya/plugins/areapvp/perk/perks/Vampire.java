package tokyo.peya.plugins.areapvp.perk.perks;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.perk.*;

import java.util.*;

public class Vampire implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.setDisplayName(new ItemStack(Material.FERMENTED_SPIDER_EYE), ChatColor.YELLOW + "Vampire");
    }

    @Override
    public List<String> getShopLore()
    {
        return Arrays.asList(
                ChatColor.GRAY + "敵を倒したときの金りんごがなくなります。",
                ChatColor.GRAY + "敵に攻撃をするたびに、" + ChatColor.RED + "0.5❤" + ChatColor.GRAY + "を回復します。",
                ChatColor.GRAY + "敵を倒した時、" + ChatColor.RED + "回復 I" + ChatColor.GRAY + " が8秒間付与されます。"
        );
    }

    @Override
    public String getName()
    {
        return "vampire";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 4000;
    }

    @Override
    public int getNeedLevel()
    {
        return 60;
    }

    @Override
    public void onRemove(Player player)
    {

    }

    @Override
    public void onBuy(Player player)
    {

    }

    @Override
    public void onWork(Player player)
    {
        if (player.hasPotionEffect(PotionEffectType.REGENERATION))
            player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 180, 0, false));
    }
}
