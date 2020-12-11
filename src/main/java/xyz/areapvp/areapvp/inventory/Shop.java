package xyz.areapvp.areapvp.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.item.IShopItem;
import xyz.areapvp.areapvp.item.Items;
import xyz.areapvp.areapvp.level.*;
import xyz.areapvp.areapvp.perk.Perks;

import java.util.Objects;

public class Shop
{
    public static void openInventory(Player player)
    {
        if (!InfoContainer.isInitialize(player))
            return;
        PlayerInfo info = InfoContainer.getInfo(player);

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(Items.items.size() / 9.0) * 9, ChatColor.BLUE + "Item Shop");

        for (IShopItem item : Items.items)
        {
            if (item.getName().equals("blank"))
                inventory.addItem(item.getItem());
            else
                inventory.addItem(ShopItem.getItem(item.getItem(), balance, item.getNeedGold(), info.prestige, item.getNeedPrestige()));

        }
        player.openInventory(inventory);
    }

    private static ItemStack getPerkItem(int slot, int need, int level, String name)
    {
        return name == null ?
                (level < need ? xyz.areapvp.areapvp.Items.setDisplayName(
                        new ItemStack(Material.BEDROCK),
                        ChatColor.RED + "Perk Slot #" + slot
                ):
                        xyz.areapvp.areapvp.Items.addMetaData(xyz.areapvp.areapvp.Items.setDisplayName(
                                new ItemStack(Material.DIAMOND_BLOCK),
                                ChatColor.YELLOW + "Perk Slot #" + slot
                        ), "slot", String.valueOf(slot))):
                Objects.requireNonNull(Perks.getPerk(name)).getItem();
    }

    public static void openPerkInventory(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player); //TODO: Info in perk
        if (info == null)
            return;

        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Perk Shop");
        inventory.setItem(11, xyz.areapvp.areapvp.Items.addMetaData(getPerkItem(1, 10, info.level, info.perk.size() < 1 ? null: info.perk.get(0)), "perkSlot", "1"));
        inventory.setItem(13, xyz.areapvp.areapvp.Items.addMetaData(getPerkItem(2, 35, info.level, info.perk.size() < 2 ? null: info.perk.get(1)), "perkSlot", "2"));
        inventory.setItem(15, xyz.areapvp.areapvp.Items.addMetaData(getPerkItem(3, 70, info.level, info.perk.size() < 3 ? null: info.perk.get(2)), "perkSlot", "3"));

        player.openInventory(inventory);
    }
}
