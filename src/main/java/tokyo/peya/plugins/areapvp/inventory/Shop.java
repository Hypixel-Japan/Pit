package tokyo.peya.plugins.areapvp.inventory;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import tokyo.peya.plugins.areapvp.Items;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.item.*;
import tokyo.peya.plugins.areapvp.perk.*;
import tokyo.peya.plugins.areapvp.player.*;

import java.util.*;

public class Shop
{
    public static void openInventory(Player player)
    {
        if (!InfoContainer.isInitialize(player))
            return;
        PlayerInfo info = InfoContainer.getInfo(player);

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(tokyo.peya.plugins.areapvp.item.Items.items.size() / 9.0) * 9, ChatColor.BLUE + "Item Shop");

        for (IShopItem item : tokyo.peya.plugins.areapvp.item.Items.items)
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
                (level < need ? tokyo.peya.plugins.areapvp.Items.setDisplayName(
                        new ItemStack(Material.BEDROCK),
                        ChatColor.RED + "Perk Slot #" + slot
                ):
                        tokyo.peya.plugins.areapvp.Items.addMetaData(tokyo.peya.plugins.areapvp.Items.setDisplayName(
                                new ItemStack(Material.DIAMOND_BLOCK),
                                ChatColor.YELLOW + "Perk Slot #" + slot
                        ), "slot", String.valueOf(slot))):
                Objects.requireNonNull(Perks.getPerk(name)).getItem();
    }

    public static void openPerkInventory(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Perk Shop");
        inventory.setItem(11, Items.addMetaData(getPerkItem(1, 10, info.level, info.perk.size() < 1 ? null: info.perk.get(0)), "perkSlot", "1"));
        inventory.setItem(13, Items.addMetaData(getPerkItem(2, 35, info.level, info.perk.size() < 2 ? null: info.perk.get(1)), "perkSlot", "2"));
        inventory.setItem(15, Items.addMetaData(getPerkItem(3, 70, info.level, info.perk.size() < 3 ? null: info.perk.get(2)), "perkSlot", "3"));

        player.openInventory(inventory);
    }
}
