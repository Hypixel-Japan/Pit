package xyz.areapvp.areapvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InventoryUtils
{
    private static void addOrElse(PlayerInventory inventory, int index, ItemStack item, boolean fwa)
    {
        if (inventory.getItem(index).getType() == Material.AIR)
            inventory.setItem(0, item);
        else if (fwa && inventory.getItem(index) != item)
            inventory.addItem(item);
    }

    public static void initItem(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        addOrElse(inventory, 0, new ItemStack(Material.IRON_SWORD), true);
        addOrElse(inventory, 1, new ItemStack(Material.BOW), true);
        addOrElse(inventory, 8, new ItemStack(Material.ARROW, 31), true);

        boolean c100n = new Random().nextBoolean();

        addOrElse(inventory, 100, new ItemStack(c100n ? Material.IRON_BOOTS: Material.CHAINMAIL_BOOTS), false);
        addOrElse(inventory, 101, new ItemStack(c100n ? Material.IRON_LEGGINGS: Material.CHAINMAIL_LEGGINGS), false);
        addOrElse(inventory, 102, new ItemStack(!c100n ? Material.IRON_CHESTPLATE: Material.CHAINMAIL_CHESTPLATE), false);

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
                initItem(player);
            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());
    }

    public static ItemStack[] equip(ItemStack[] current)
    {
        ArrayList<ItemStack> newI = new ArrayList<>();
        for (ItemStack stack: current)
        {
            if (stack == null || stack.getType() == Material.AIR || stack.getItemMeta() == null || !stack.getItemMeta().hasLore())
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
