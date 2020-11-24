package xyz.areapvp.areapvp.perk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.inventory.ShopItem;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

public class PerkInventory
{
    public static Inventory getPerksInventory(Player player, int slot)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return Bukkit.createInventory(null, 9, "ERROR");

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, ((Perks.perks.size() % 9 == 0 ? 1: Perks.perks.size() % 9) * 9) + 9, ChatColor.BLUE + "Perk Shop");
        for (IPerkEntry item : Perks.perks)
        {
            if (info.perk.contains(item.getName()))
                inventory.addItem(Items.addMetaData(Items.addMetaData(Items.quickLore(Items.addGlow(Items.lore(item.getItem(), item.getShopLore())), ChatColor.RED + "あなたはすでにこのPerkを適用しています！"), "slot", String.valueOf(slot)), "type", item.getName()));
            else if (info.ownPerk.contains(item.getName()))
            {
                inventory.addItem(Items.addMetaData(Items.addMetaData(Items.quickLore(Items.lore(item.getItem(), item.getShopLore()), ChatColor.YELLOW + "クリックして適用！"), "slot", String.valueOf(slot)), "type", item.getName()));
            }
            else
            {
                ItemStack stack = ShopItem.getItem(
                        item.getItem(),
                        balance,
                        item.getNeedGold(),
                        info.prestige,
                        item.getNeedPrestige()
                );
                inventory.addItem(Items.addMetaData(Items.addMetaData(Items.lore(stack, item.getShopLore()), "slot", String.valueOf(slot)), "type", item.getName()));
            }
        }

        inventory.setItem(inventory.getSize() - 5, Items.addMetaData(Items.addMetaData(Items.setDisplayName(new ItemStack(Material.DIAMOND_BLOCK), "Perkなし"), "slot", String.valueOf(slot)), "type", "noPerk"));

        return inventory;
    }

}
