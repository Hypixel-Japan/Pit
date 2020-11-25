package xyz.areapvp.areapvp.perk.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.perk.IPerkEntry;

import java.util.Collections;
import java.util.List;

public class EndlessQuiver implements IPerkEntry
{
    @Override
    public ItemStack getItem()
    {
        return Items.setDisplayName(new ItemStack(Material.BOW), ChatColor.GREEN + "無限の矢筒");
    }

    @Override
    public List<String> getShopLore()
    {
        return Collections.singletonList(ChatColor.GRAY + "プレイヤーに矢がヒットする毎に、" + ChatColor.WHITE + "+3矢" +
                ChatColor.GRAY + "をプレイヤーに付与します。");
    }

    @Override
    public String getName()
    {
        return "endlessQuiver";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 2000;
    }

    @Override
    public int getNeedLevel()
    {
        return 20;
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
        player.getInventory().addItem(new ItemStack(Material.ARROW,  3));
    }
}
