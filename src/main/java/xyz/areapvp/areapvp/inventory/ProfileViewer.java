package xyz.areapvp.areapvp.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.*;
import xyz.areapvp.areapvp.player.Exp;
import xyz.areapvp.areapvp.player.PlayerInfo;
import xyz.areapvp.areapvp.player.PlayerModify;
import xyz.areapvp.areapvp.perk.Perks;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class ProfileViewer
{
    public static void viewPlayer(Player player, Player viewer)
    {
        if (player == null)
            return;

        PlayerInfo info = PlayerModify.getInfo(player); //TODO: Perk in InfoContainer

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
        if (info == null)
        {
            viewer.openInventory(inventory);
            AreaPvP.gui.put(viewer.getUniqueId(), "profile");
            return;
        }

        int[] i = {12};

        info.perk.parallelStream()
                .forEach(s -> inventory.setItem(
                        ++i[0],
                        Items.lore(Items.addMetaData(Objects.requireNonNull(Perks.getPerk(s)).getItem(),
                                "AreaPvP::NotPickable", "true"
                                )
                                , Collections.emptyList())
                ));
        IntStream.range(0, 4).forEach(value -> {
            if (inventory.getItem(value + 13) == null)
                inventory.setItem(value + 13, Items.setDisplayName(
                        Items.addMetaData(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "AreaPvP::notPickable", "1b"),
                        ChatColor.YELLOW + "#" + (value + 1) + " Perk slot"
                ));
        });
        //NameTag
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        stack = Items.addMetaData(stack, "AreaPvP::NotPickable", "true");
        stack = Items.setDisplayName(stack, PlayerInfo.getPrefixFull(info.level, info.prestige) + " " +
                ChatColor.GRAY + player.getName());

        BigDecimal decimal = BigDecimal.valueOf(AreaPvP.economy.getBalance(player));

        String decimalOf;

        if (decimal.compareTo(new BigDecimal(1000)) <= 0)
            decimalOf = String.valueOf(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        else
            decimalOf = String.format("%,d", decimal.setScale(1, BigDecimal.ROUND_HALF_UP).longValue());

        long exp = Exp.getExp(info.level, info.prestige) + info.exp;

        stack = Items.lore(stack, Arrays.asList(
                ChatColor.GRAY + "Gold: " + ChatColor.GOLD + decimalOf + "g",
                ChatColor.GRAY + "Total XP: " + ChatColor.AQUA + exp + " XP"
        ));

        inventory.setItem(20, stack);
        //Inventory
        inventory.setItem(
                23,
                Items.addMetaData(Items.lore(
                        Items.setDisplayName(
                                Items.addMetaData(new ItemStack(Material.CHEST),
                                        "AreaPvP::type", "inv"
                                ),
                                ChatColor.GREEN + "Invenotry"
                        ),
                        Arrays.asList(
                                ChatColor.GRAY + "プレイヤーのインベントリをチェックします！",
                                "",
                                ChatColor.YELLOW + "クリックして開く！"
                        )
                ), "AreaPvP::uuid", player.getUniqueId().toString())
        );
        //EnderChest
        inventory.setItem(
                24,
                Items.addMetaData(Items.lore(
                        Items.setDisplayName(
                                Items.addMetaData(new ItemStack(Material.ENDER_CHEST),
                                        "AreaPvP::type", "ender"
                                ),
                                ChatColor.DARK_PURPLE + "Ender Chest"
                        ),
                        Arrays.asList(
                                ChatColor.GRAY + "プレイヤーのエンダーチェストをチェックします！",
                                "",
                                ChatColor.YELLOW + "クリックして開く！"
                        )
                ), "AreaPvP::uuid", player.getUniqueId().toString())
        );

        viewer.openInventory(inventory);
        AreaPvP.gui.put(viewer.getUniqueId(), "profile");
    }

    private static Inventory getInv(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 36, "インベントリ");
        if (player == null)
            return inventory;
        IntStream.range(0, 36)
                .forEach(value -> {
                    ItemStack stack = player.getInventory().getItem(value);
                    if (stack != null)
                        inventory.setItem(value, Items.addMetaData(stack, "AreaPvP::NotPickable", "1b"));
                });
        return inventory;
    }

    public static Inventory getEnder(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 27, "エンダーチェスト");
        IntStream.range(0, 26)
                .forEach(value -> {
                    ItemStack stack = player.getEnderChest().getItem(value);
                    if (stack != null)
                        inventory.setItem(value, Items.addMetaData(stack, "AreaPvP::NotPickable", "1b"));
                });
        return inventory;
    }

    public static void onPickUp(Player player, ItemStack stack)
    {
        if (Items.hasMetadata(stack, "AreaPvP::NotPickable"))
            return;
        String type;
        if ((type = Items.getMetadata(stack, "AreaPvP::type")) == null)
            return;

        String uuid;
        if ((uuid = Items.getMetadata(stack, "AreaPvP::uuid")) == null)
            return;
        if (type.equals("inv"))
        {
            player.closeInventory();
            player.openInventory(getInv(Bukkit.getPlayer(UUID.fromString(uuid))));
            AreaPvP.gui.put(player.getUniqueId(), "profile");
        }
        else if (type.equals("ender"))
        {
            player.closeInventory();
            player.openInventory(getEnder(Bukkit.getPlayer(UUID.fromString(uuid))));
            AreaPvP.gui.put(player.getUniqueId(), "profile");
        }
    }
}
