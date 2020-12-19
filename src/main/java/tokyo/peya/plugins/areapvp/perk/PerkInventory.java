package tokyo.peya.plugins.areapvp.perk;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.inventory.*;
import tokyo.peya.plugins.areapvp.player.*;

public class PerkInventory
{
    public static Inventory getPerksInventory(Player player, int slot)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return Bukkit.createInventory(null, 9, "ERROR");

        int balance = (int) AreaPvP.economy.getBalance(player);

        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.BLUE + "Perk Shop");
        for (IPerkEntry item : Perks.perks)
        {
            if (info.perk.contains(item.getName()))
                inventory.addItem(Items.addMetaData(Items.addMetaData(Items.quickLore(Items.addGlow(Items.lore(item.getItem(), item.getShopLore())), ChatColor.RED + "あなたはすでにこのPerkを適用しています！"), "slot", String.valueOf(slot)), "type", item.getName()));
            else if (info.ownPerk.contains(item.getName()))
                inventory.addItem(Items.addMetaData(Items.addMetaData(Items.quickLore(Items.lore(item.getItem(), item.getShopLore()), ChatColor.YELLOW + "クリックして適用！"), "slot", String.valueOf(slot)), "type", item.getName()));
            else
            {
                ItemStack stack = ShopItem.getItem(
                        item.getItem(),
                        balance,
                        item.getNeedGold(),
                        info.prestige,
                        item.getNeedPrestige()
                );

                stack = Items.addMetaData(Items.addMetaData(Items.lore(stack, item.getShopLore()), "slot", String.valueOf(slot)), "type", item.getName());

                stack = ShopItem.getItem(stack, balance, item.getNeedGold(), info.prestige, item.getNeedPrestige(), info.level, item.getNeedLevel());

                inventory.addItem(stack);
            }
        }

        inventory.setItem(inventory.getSize() - 5, Items.addMetaData(Items.addMetaData(Items.setDisplayName(new ItemStack(Material.DIAMOND_BLOCK), "Perkなし"), "slot", String.valueOf(slot)), "type", "noPerk"));

        return inventory;
    }

}
