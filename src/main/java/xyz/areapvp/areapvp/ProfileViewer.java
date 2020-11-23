package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.level.PlayerInfo;

public class ProfileViewer
{
    static void viewPlayer(Player player, Player viewer)
    {
        Inventory inventory = Bukkit.createInventory(null, 45, "Profile Viewer");
        //Armor
        inventory.setItem(0, Items.addMetaData(player.getInventory().getHelmet(), "AreaPvP::NotPickable", "true"));
        inventory.setItem(9, Items.addMetaData(player.getInventory().getChestplate(), "AreaPvP::NotPickable", "true"));
        inventory.setItem(18, Items.addMetaData(player.getInventory().getLeggings(), "AreaPvP::NotPickable", "true"));
        inventory.setItem(27, Items.addMetaData(player.getInventory().getBoots(), "AreaPvP::NotPickable", "true"));
        //HotBar
        inventory.setItem(36, Items.addMetaData(player.getInventory().getItem(0), "AreaPvP::NotPickable", "true"));
        inventory.setItem(37, Items.addMetaData(player.getInventory().getItem(1), "AreaPvP::NotPickable", "true"));
        inventory.setItem(38, Items.addMetaData(player.getInventory().getItem(2), "AreaPvP::NotPickable", "true"));
        inventory.setItem(39, Items.addMetaData(player.getInventory().getItem(3), "AreaPvP::NotPickable", "true"));
        inventory.setItem(40, Items.addMetaData(player.getInventory().getItem(4), "AreaPvP::NotPickable", "true"));
        inventory.setItem(41, Items.addMetaData(player.getInventory().getItem(5), "AreaPvP::NotPickable", "true"));
        inventory.setItem(42, Items.addMetaData(player.getInventory().getItem(6), "AreaPvP::NotPickable", "true"));
        inventory.setItem(43, Items.addMetaData(player.getInventory().getItem(7), "AreaPvP::NotPickable", "true"));
        inventory.setItem(44, Items.addMetaData(player.getInventory().getItem(8), "AreaPvP::NotPickable", "true"));
        //Info
        inventory.setItem(
                11,
                Items.addMetaData(Items.setDisplayName(PlayerInfo.getHead(player.getName()), ChatColor.GRAY + player.getName()),
                        "AreaPvP::NotPickable", "true"
                )
        );

        viewer.openInventory(inventory);
        AreaPvP.gui.put(viewer.getUniqueId(), "profile");
    }

    public static void onPickUp(Player player, ItemStack stack)
    {
        if (Items.hasMetadata(stack, "AreaPvP::NotPickable"))
            return;
    }
}
