package xyz.areapvp.areapvp;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.areapvp.areapvp.perk.Perk;
import xyz.areapvp.areapvp.perk.Perks;

import java.util.ArrayList;
import java.util.Arrays;
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
        addOrElse(inventory, 0, Items.noDrop(Items.changeDamage(Items.setUnbreakable(new ItemStack(Material.IRON_SWORD)), 6)), true);
        addOrElse(inventory, 1, Items.noDrop(Items.setUnbreakable(new ItemStack(Material.BOW))), true);
        addOrElse(inventory, 8, new ItemStack(Material.ARROW, 32), true);

        if (inventory.getBoots() == null)
            inventory.setBoots(Items.setUnbreakable(new ItemStack(new Random().nextBoolean() ? Material.IRON_BOOTS: Material.CHAINMAIL_BOOTS)));
        if (inventory.getLeggings() == null)
            inventory.setLeggings(Items.setUnbreakable(new ItemStack(new Random().nextBoolean() ? Material.IRON_LEGGINGS: Material.CHAINMAIL_LEGGINGS)));
        if (inventory.getChestplate() == null)
            inventory.setChestplate(Items.setUnbreakable(new ItemStack(new Random().nextBoolean() ? Material.IRON_CHESTPLATE: Material.CHAINMAIL_CHESTPLATE)));
        if (Perk.contains(player, "mineMan"))
            player.getInventory().addItem(Items.noDrop(new ItemStack(Material.COBBLESTONE, 24)));
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
                Arrays.stream(inventory.getContents())
                        .parallel()
                        .forEach(stack -> {
                            if (stack == null)
                                return;
                            if (!Items.hasMetadata(stack, "keptOnDeath") && !Items.hasMetadata(stack, "perk"))
                                stack.setAmount(0);
                        });
                initItem(player);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }.runTaskAsynchronously(AreaPvP.getPlugin());
    }

    public static ItemStack[] equip(ItemStack[] current)
    {
        ArrayList<ItemStack> newI = new ArrayList<>();
        for (ItemStack stack : current)
        {
            if (stack == null || stack.getType() == Material.AIR || stack.getItemMeta() == null || !stack.getItemMeta().hasLore())
            {
                newI.add(new ItemStack(Material.AIR));
                continue;
            }

            if (Items.hasMetadata(stack, "keptOnDeath") || Items.hasMetadata(stack, "perk"))
                newI.add(stack);
        }

        return newI.toArray(new ItemStack[0]);
    }
}
