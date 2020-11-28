package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.areapvp.areapvp.level.PlayerInfo;
import xyz.areapvp.areapvp.level.PlayerModify;

import java.util.Arrays;

public class Prestige
{
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
        stack = Items.lore(stack, Arrays.asList(
                info.prestige > 1 ? ChatColor.GRAY + "Current: " + ChatColor.YELLOW + PlayerInfo.arabicToRoman(info.prestige): "",
                ChatColor.GRAY + "Required Level: " + PlayerInfo.getPrefix(120, info.prestige),
                ChatColor.GRAY + "Costs:",
                ChatColor.RED + "◎" + ChatColor.AQUA + "Level" +
                        ChatColor.RED + "が 1 に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.GOLD + "Gold" +
                        ChatColor.RED + "が 0 に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.RED +
                        "すべての" + ChatColor.GREEN + "Perkが" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.RED + ChatColor.BOLD.toString() + "◎" + ChatColor.RESET + ChatColor.AQUA + "Level" +
                        ChatColor.RED + "が1に" + ChatColor.BOLD + "リセット" + ChatColor.RESET + ChatColor.RED + "されます。",
                ChatColor.GRAY + ChatColor.ITALIC.toString() + "エンダーチェストの中身はリセットされません。",
                ChatColor.GRAY + ChatColor.ITALIC.toString() + "スペシャルアイテムまたは、倒されても消えないアイテムはリセットされません。"
                ));
        stack = Items.addMetaData(stack, "action", "prestige");
        inventory.setItem(13, stack);

    }
}
