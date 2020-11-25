package xyz.areapvp.areapvp.perk;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IPerkEntry
{
    ItemStack getItem();

    List<String> getShopLore();

    String getName();

    int getNeedPrestige();

    int getNeedGold();

    int getNeedLevel();

    void onBuy(Player player);

    void onWork(Player player);
}
