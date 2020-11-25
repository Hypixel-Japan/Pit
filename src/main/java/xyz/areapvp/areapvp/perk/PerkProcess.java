package xyz.areapvp.areapvp.perk;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.Items;
import xyz.areapvp.areapvp.Kill;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PerkProcess implements Listener
{
    @EventHandler
    private static void onArrowHit(EntityDamageByEntityEvent e)
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


    private static long countItem(Player player, String meta)
    {
        return Arrays.stream(player.getInventory().getContents())
                .parallel()
                .filter(stack -> Items.getMetadata(stack, "type") != null &&
                        Items.getMetadata(stack, "type").equals(meta))
                .mapToLong(ItemStack::getAmount).sum();
    }

    public static void onKill(Player deather)
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

        if (Perk.contains(killer, "gHead"))
        {
            if (countItem(killer, "gHead") < 2)
                killer.getInventory().addItem(Items.addMetaData(Objects.requireNonNull(Perks.getPerk("gHead")).getItem(), "type", "gHead"));
        }
        else if (countItem(killer, "gapple") < 2)
            killer.getInventory().addItem(Items.addMetaData(new ItemStack(Material.GOLDEN_APPLE), "type", "gapple"));

    }

    @EventHandler
    public static void onFood(PlayerItemConsumeEvent e)
    {
        String type;
        if ((type = Items.getMetadata(e.getItem(), "type")) == null)
        {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        Player player = e.getPlayer();

        ItemStack stack = e.getItem().clone();
        stack.setAmount(1);

        switch (type)
        {
            case "gapple":
                player.getInventory().remove(stack);
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
    public static void onInteract(PlayerInteractEvent e)
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
}
