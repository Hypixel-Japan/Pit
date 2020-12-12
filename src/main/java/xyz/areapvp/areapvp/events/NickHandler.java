package xyz.areapvp.areapvp.events;

import net.dev.eazynick.api.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.scoreboard.*;
import xyz.areapvp.areapvp.*;
import xyz.areapvp.areapvp.player.*;

public class NickHandler implements Listener
{
    @EventHandler
    public void onNick(PlayerNickEvent e)
    {
        InfoContainer.nick(e.getPlayer());
        AreaPvP.refreshScoreBoard(e.getPlayer());
        e.getPlayer().sendMessage(ChatColor.YELLOW + "あなたはニックネームを使用していますので、表示されるレベルがランダム化されています！");
        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onUnnick(PlayerUnnickEvent e)
    {
        InfoContainer.unnick(e.getPlayer());
        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        AreaPvP.refreshScoreBoard(e.getPlayer());
    }
}
