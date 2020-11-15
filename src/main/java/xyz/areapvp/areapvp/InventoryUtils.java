package xyz.areapvp.areapvp;

import jdk.internal.dynalink.beans.StaticClass;
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
        if (inventory.getItem(index) == null || inventory.getItem(index).getType() == Material.AIR)
            inventory.setItem(index, item);
        else if (fwa && inventory.getItem(index) != item)
            inventory.addItem(item);
    }

    public static void initItem(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        addOrElse(inventory, 0, Items.setUnbreakable(new ItemStack(Material.IRON_SWORD)), true);
        addOrElse(inventory, 1, Items.setUnbreakable(new ItemStack(Material.BOW)), true);
        addOrElse(inventory, 8, Items.setUnbreakable(new ItemStack(Material.ARROW, 31)), true);

        boolean c100n = new Random().nextBoolean();

        if (inventory.getBoots() == null)
            inventory.setBoots(Items.setUnbreakable(new ItemStack(c100n ? Material.IRON_BOOTS: Material.CHAINMAIL_BOOTS)));
        if (inventory.getLeggings() == null)
            inventory.setLeggings(Items.setUnbreakable(new ItemStack(c100n ? Material.IRON_LEGGINGS: Material.CHAINMAIL_LEGGINGS)));
        if (inventory.getChestplate() == null)
            inventory.setChestplate(Items.setUnbreakable(new ItemStack(!c100n ? Material.IRON_CHESTPLATE: Material.CHAINMAIL_CHESTPLATE)));

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

            if (Items.hasMetadata(stack, "keptOnDeath") || Items.hasMetadata(stack, "special"))
                newI.add(stack);
        }

        return newI.toArray(new ItemStack[0]);
    }
}
