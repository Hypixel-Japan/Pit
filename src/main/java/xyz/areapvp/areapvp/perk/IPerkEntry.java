package xyz.areapvp.areapvp.perk;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import java.util.*;

public interface IPerkEntry
{
    ItemStack getItem();

    List<String> getShopLore();

    String getName();

    int getNeedPrestige();

    int getNeedGold();

    int getNeedLevel();

    void onRemove(Player player);

    void onBuy(Player player);

    void onWork(Player player);
}
