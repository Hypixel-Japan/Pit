package xyz.areapvp.areapvp.perk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.play.Kill;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PerkProcess implements Listener
{

    @EventHandler
    private static void onInvClick(InventoryClickEvent e) //Ender-Chest
    {
        Player player = (Player) e.getWhoClicked();
        if (!e.getInventory().equals(player.getEnderChest()))
            return;

        if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY)
            return;

        ItemStack itemStack = e.getCurrentItem();

        String type;
        if ((type = Items.getMetadata(itemStack, "enderChest")) == null)
            return;

        if (type.equals("false") || Items.hasMetadata(itemStack, "perk"))
        {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "このアイテムはエンダーチェストに収納できません！");
        }
    }

    @EventHandler
    private static void onDrag(InventoryDragEvent e) //Ender-Chest
    {
        System.out.println("a");
        Player player = (Player) e.getWhoClicked();
        if (!e.getInventory().equals(player.getEnderChest()))
            return;

        ItemStack itemStack = e.getCursor();

        if (itemStack == null)
            return;

        String type;
        if ((type = Items.getMetadata(itemStack, "enderChest")) == null)
            return;

        if (type.equals("false") || Items.hasMetadata(itemStack, "perk"))
        {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "このアイテムはエンダーチェストに収納できません！");
        }
    }

    @EventHandler
    private static void onHit(EntityDamageByEntityEvent e) //Vampire
    {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
            return;

        Player hitter = (Player) e.getDamager();
        Player player = (Player) e.getEntity();

        if (Perk.contains(hitter, "vampire"))
            if (hitter.getHealth() > 19)
                hitter.setHealth(20);
            else
                hitter.setHealth(hitter.getHealth() + 1);
    }

    @EventHandler
    private static void onArrowHit(EntityDamageByEntityEvent e) //EndlessQuiver
    {
        if (e.getDamager().getType() != EntityType.ARROW)
            return;

        if (!(e.getEntity() instanceof Player))
            return;

        Arrow arrow = (Arrow) e.getDamager();

        if (!(arrow.getShooter() instanceof Player))
            return;

        Player shooter = (Player) arrow.getShooter();
        Player damager = (Player) e.getEntity();

        if (shooter.getUniqueId() == damager.getUniqueId())
            return;

        if (Perk.contains(shooter, "endlessQuiver"))
            Objects.requireNonNull(Perks.getPerk("endlessQuiver")).onWork(shooter);
    }

    public static long countItem(Player player, Material type)
    {
        if (type == null)
            return 0;
        return Arrays.stream(player.getInventory().getContents())
                .parallel()
                .filter(stack -> stack != null && stack.getType() == type)
                .mapToLong(ItemStack::getAmount).sum();
    }

    public static long countItem(Player player, String meta)
    {
        return Arrays.stream(player.getInventory().getContents())
                .parallel()
                .filter(stack -> Items.getMetadata(stack, "type") != null &&
                        Items.getMetadata(stack, "type").equals(meta))
                .mapToLong(ItemStack::getAmount).sum();
    }

    public static void onKill(Player deather) //Vampire, G-Head, Gapple, MineMan
    {
        Player killer = deather.getKiller();
        if (killer == null)
        {

            if (!deather.hasMetadata("x-hitter"))
                return;

            String uuid = null;
            for (MetadataValue hitter : deather.getMetadata("x-hitter"))
                if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                    uuid = hitter.asString();
            if (uuid == null)
                return;

            killer = Bukkit.getPlayer(UUID.fromString(uuid));
        }

        if (Kill.hasReduce(killer))
            return;

        if (Perk.contains(killer, "vampire"))
            Objects.requireNonNull(Perks.getPerk("vampire")).onWork(killer);
        else if (Perk.contains(killer, "gHead"))
        {
            if (countItem(killer, "gHead") < 2)
                killer.getInventory().addItem(Items.addMetaData(Objects.requireNonNull(Perks.getPerk("gHead")).getItem(), "type", "gHead"));
        }
        else if (countItem(killer, "gapple") < 2)
            killer.getInventory().addItem(Items.addMetaData(new ItemStack(Material.GOLDEN_APPLE), "type", "gapple"));

        if (Perk.contains(killer, "mineMan"))
            Objects.requireNonNull(Perks.getPerk("mineMan")).onWork(killer);

    }

    @EventHandler
    public static void onFood(PlayerItemConsumeEvent e) //Gapple
    {
        String type;
        if ((type = Items.getMetadata(e.getItem(), "type")) == null)
        {
            e.setCancelled(true);
            return;
        }


        Player player = e.getPlayer();

        switch (type)
        {
            case "gapple":
                if (player.hasPotionEffect(PotionEffectType.ABSORPTION))
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 114514, 1, false));
                player.damage(2);

                if (player.hasPotionEffect(PotionEffectType.REGENERATION))
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1, false));
                break;
        }

    }

    @EventHandler
    public static void onInteract(PlayerInteractEvent e) //G-Head
    {
        Player player = e.getPlayer();

        ItemStack stack = e.getItem();
        if (stack == null || stack.getType() == Material.AIR)
            return;
        ItemStack st = stack.clone();
        String type;
        if ((type = Items.getMetadata(st, "type")) == null)
            return;
        switch (type.toLowerCase())
        {
            case "ghead":
                e.getItem().setAmount(e.getItem().getAmount() - 1);
                e.setCancelled(true);
                Objects.requireNonNull(Perks.getPerk("gHead")).onWork(player);
                break;
        }
    }

    @EventHandler
    public static void onDrop(PlayerDropItemEvent e) //Remove chain items
    {
        if (e.getItemDrop().getItemStack().getType() == Material.CHAINMAIL_BOOTS ||
                e.getItemDrop().getItemStack().getType() == Material.CHAINMAIL_CHESTPLATE ||
                e.getItemDrop().getItemStack().getType() == Material.CHAINMAIL_HELMET ||
                e.getItemDrop().getItemStack().getType() == Material.CHAINMAIL_LEGGINGS)
            e.getItemDrop().remove();
    }
}
