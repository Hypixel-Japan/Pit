package xyz.areapvp.areapvp;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.level.*;

import java.util.Arrays;

public class Prestige
{
    private static String[] getCautionLore()
    {
        return  new String[]{ChatColor.RED + "◎" + ChatColor.AQUA + "Level" +
                ChatColor.RED + "が 1 に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.GOLD + "Gold" +
                ChatColor.RED + "が 0 に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.RED +
                        "すべての" + ChatColor.GREEN + "Perkが" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.AQUA + "Level" +
                        ChatColor.RED + "が1に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.GRAY + ChatColor.ITALIC.toString() + "エンダーチェストの中身はリセットされません。",
                ChatColor.GRAY + ChatColor.ITALIC.toString() + "スペシャルアイテムまたは、倒されても消えないアイテムはリセットされません。"};
    }

    public static void openInventry(Player player)
    {
        PlayerInfo info = PlayerModify.getInfo(player);
        if (info == null)
            return;

        if (info.prestige == 0 && info.level < 120)
        {
            player.sendMessage(ChatColor.RED + "LVが低すぎます！Lv120になるまでこの機能は使用できません！");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, "Prestige");

        ItemStack stack = new ItemStack(Material.DIAMOND);
        stack = Items.setDisplayName(stack, ChatColor.AQUA + "Prestige");
        stack = Items.lore(stack, Arrays.asList((String[]) ArrayUtils.addAll(
                new String[]{info.prestige > 1 ? ChatColor.GRAY + "Current: " + ChatColor.YELLOW + PlayerInfo.arabicToRoman(info.prestige): "",
                        ChatColor.GRAY + "Required Level: " + PlayerInfo.getPrefix(120, info.prestige),
                        ChatColor.GRAY + "Costs:"}, getCautionLore())));

        stack = Items.addMetaData(stack, "action", "prestige");
        stack = Items.addMetaData(stack, "phase", "do");
        inventory.setItem(13, stack);
        AreaPvP.gui.put(player.getUniqueId(), "prestige");
        player.openInventory(inventory);
    }

    public static Inventory getConfirmPresInventory(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 27, "Are you sure?");
        inventory.setItem(11,
                Items.addMetaData(Items.addMetaData(Items.lore(Items.setDisplayName(new ItemStack(Material.STAINED_CLAY, 1, (short) 13),
                        ChatColor.DARK_GREEN + "Confirm"), Arrays.asList(getCautionLore()))
                        , "action", "prestige")
                        , "phase", "confirm"));
        inventory.setItem(15,
                Items.addMetaData(Items.addMetaData(Items.setDisplayName(new ItemStack(Material.STAINED_CLAY, 1, (short) 14),
                        ChatColor.DARK_RED + "Cancel"),
                        "action", "prestige"),
                        "phase", "cancel"));
        return inventory;
    }


    public static void onPickUp(Player player, ItemStack stack)
    {
        if (!Items.hasMetadata(stack, "phase") || !Items.hasMetadata(stack, "action"))
            return;
        String phase = Items.getMetadata(stack, "phase");
        String action = Items.getMetadata(stack, "action");
        switch (action)
        {
            case "prestige":
                switch (phase)
                {
                    case "do":
                        if (!InfoContainer.isInitialize(player))
                            return;
                        PlayerInfo info = InfoContainer.getInfo(player);
                        if (info.level < 120)
                        {
                            player.sendMessage(ChatColor.RED + "あなたはまだPrestigeできません！");
                            return;
                        }

                        player.openInventory(getConfirmPresInventory(player));
                        AreaPvP.gui.put(player.getUniqueId(), "prestige");
                        return;
                    case "cancel":
                        player.sendMessage(ChatColor.RED + "Prestigeをキャンセルしました！");
                        player.closeInventory();
                        return;
                    case "confirm":
                        PlayerModify.addPrestige(player);
                        player.closeInventory();
                        return;
                }
        }
    }
}
