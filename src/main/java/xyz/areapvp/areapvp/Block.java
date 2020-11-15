package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Block implements Listener {
    @EventHandler
    private void onBreak(BlockBreakEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE){

        }else {
            if(e.getBlock().hasMetadata( "newPlayer" )){

            }else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onPlace(BlockPlaceEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE){

        }else {
            e.getBlock().setMetadata( "newPlayer", new FixedMetadataValue( AreaPvP.getPlugin(),"binzyouozisan" ) );
        }
    }

}