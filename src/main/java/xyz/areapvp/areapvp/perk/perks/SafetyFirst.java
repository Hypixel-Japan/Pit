package xyz.areapvp.areapvp.perk.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.perk.IPerkEntry;

import java.util.Collections;
import java.util.List;

public class SafetyFirst implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.setPerk(Items.setUnbreakable(new ItemStack(Material.CHAINMAIL_HELMET)));
    }

    @Override
    public List<String> getShopLore()
    {
        return Collections.singletonList(ChatColor.GRAY + "ヘルメットをスポーンします。");
    }

    @Override
    public String getName()
    {
        return "safetyFirst";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 3000;
    }

    @Override
    public int getNeedLevel()
    {
        return 30;
    }

    @Override
    public void onRemove(Player player)
    {
        if (player.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET)
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().remove(Material.CHAINMAIL_HELMET);
    }

    @Override
    public void onBuy(Player player)
    {
        if (player.getInventory().getHelmet() == null)
            player.getInventory().setHelmet(getItem());
        else if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET ||
        player.getInventory().getHelmet().getType() == Material.GOLD_HELMET)
        {
            ItemStack helm = player.getInventory().getHelmet().clone();
            player.getInventory().setHelmet(getItem());
            player.getInventory().addItem(helm);
        }
    }

    @Override
    public void onWork(Player player)
    {

    }
}
