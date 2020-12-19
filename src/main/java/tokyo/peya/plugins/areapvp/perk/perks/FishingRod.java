package tokyo.peya.plugins.areapvp.perk.perks;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.perk.*;

import java.util.*;

public class FishingRod implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.setPerk(Items.setUnbreakable(Items.noDrop(new ItemStack(Material.FISHING_ROD))));
    }

    @Override
    public List<String> getShopLore()
    {
        return Collections.singletonList(ChatColor.GRAY + "釣り竿をスポーンします。");
    }

    @Override
    public String getName()
    {
        return "fishingRod";
    }

    @Override
    public int getNeedLevel()
    {
        return 10;
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 1000;
    }

    @Override
    public void onRemove(Player player)
    {
        player.getInventory().remove(getItem());
    }

    @Override
    public void onBuy(Player player)
    {
        player.getInventory().addItem(getItem());
    }

    @Override
    public void onWork(Player player)
    {

    }
}
