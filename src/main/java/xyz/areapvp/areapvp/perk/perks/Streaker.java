package xyz.areapvp.areapvp.perk.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.perk.IPerkEntry;

import java.util.Collections;
import java.util.List;

public class Streaker implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.setDisplayName(new ItemStack(Material.WHEAT), ChatColor.GREEN + "Streaker");
    }

    @Override
    public List<String> getShopLore()
    {
        return Collections.singletonList(ChatColor.GRAY + "3ストリーク以上のとき、Streak BonusEXPが3倍になります。");
    }

    @Override
    public String getName()
    {
        return "streaker";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 8000;
    }

    @Override
    public int getNeedLevel()
    {
        return 50;
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

    }
}
