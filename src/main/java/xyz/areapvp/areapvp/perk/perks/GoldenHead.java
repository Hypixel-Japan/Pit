package xyz.areapvp.areapvp.perk.perks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.perk.IPerkEntry;

public class GoldenHead implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
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
    public void onRemove()
    {

    }

    @Override
    public void onWork(Player player)
    {

    }
}
