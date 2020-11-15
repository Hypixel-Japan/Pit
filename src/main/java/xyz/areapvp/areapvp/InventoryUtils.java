package xyz.areapvp.areapvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import javax.lang.model.util.ElementScanner6;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class InventoryUtils
{
    private static void addOrElse(PlayerInventory inventory, int index, ItemStack item)
    {
        if (inventory.getItem(index).getType() == Material.AIR)
            inventory.setItem(0, item);
        else if (inventory.getItem(index) != item)
            inventory.addItem(item);
    }

    public static void initItem(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        addOrElse(inventory, 0, new ItemStack(Material.IRON_SWORD));
        addOrElse(inventory, 1, new ItemStack(Material.BOW));
        addOrElse(inventory, 8, new ItemStack(Material.ARROW, 31));
    }
    public static void reItem(Player player)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                PlayerInventory inventory = player.getInventory();
                inventory.setArmorContents(equip(inventory.getArmorContents()));
                inventory.setStorageContents(equip(inventory.getStorageContents()));
            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());
    }

    public static ItemStack[] equip(ItemStack[] current)
    {
        ArrayList<ItemStack> newI = new ArrayList<>();
        for (ItemStack stack: current)
        {
            if (stack.getItemMeta() == null || !stack.getItemMeta().hasLore())
            {
                newI.add(new ItemStack(Material.AIR));
                continue;
            }
            List<String> lore = stack.getItemMeta().getLore();

            if (lore.get(0).equals(Items.keptOnDeath) || lore.get(0).equals(Items.specialItem) || lore.get(0).equals(Items.perkItem))
                newI.add(stack);
        }

        return newI.toArray(new ItemStack[0]);
    }
}
