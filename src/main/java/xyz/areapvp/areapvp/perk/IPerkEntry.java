package xyz.areapvp.areapvp.perk;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPerkEntry
{
    ItemStack getItem();
    String getName();
    int getNeedPrestige();
    int getNeedGold();
    void onBuy(Player player);
    void onSell();
}
