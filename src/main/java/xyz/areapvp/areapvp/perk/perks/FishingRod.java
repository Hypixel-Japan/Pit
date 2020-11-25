package xyz.areapvp.areapvp.perk.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.perk.IPerkEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FishingRod implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.noDrop(new ItemStack(Material.FISHING_ROD));
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
        return 0;
    }

    @Override
    public void onBuy(Player player)
    {

    }

    @Override
    public void onWork(Player player)
    {

    }
}
