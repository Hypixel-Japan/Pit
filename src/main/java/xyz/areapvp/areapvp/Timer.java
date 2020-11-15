package xyz.areapvp.areapvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.geom.Area;
import java.util.ArrayList;

public class Timer extends BukkitRunnable
{
    @Override
    public void run()
    {
        breakBlock();
    }

    private void hit()
    {
        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> {
                    if (!player.hasMetadata("x-hitted"))
                    {
                        if (!player.hasMetadata("x-hitter"))
                            return;
                        player.removeMetadata("x-hitter", AreaPvP.getPlugin());
                        return;
                    }

                    int hitted = 15;
                    for (MetadataValue hitter: player.getMetadata("x-hitted"))
                        if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                            hitted = hitter.asInt();
                    hitted = hitted - 1;
                    player.removeMetadata("x-hitted", AreaPvP.getPlugin());
                    player.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), hitted));
                });
    }

    private void breakBlock()
    {
        ArrayList<Location> removeKeys = new ArrayList<>();
        AreaPvP.blockPlace
                .forEach((key, value) -> {

                    if (value == null)
                        return; //削除しないブロック(ADMINが遊んでる場合など)
                    int val = value - 1;
                    if (val > 0)
                    {
                        AreaPvP.blockPlace.put(key, val);
                        return;
                    }

                    removeKeys.add(key);
                    key.getWorld().getBlockAt(key).removeMetadata("newPlayer", AreaPvP.getPlugin());
                    key.getWorld().getBlockAt(key).setType(Material.AIR);

                });
        removeKeys.parallelStream().forEach(AreaPvP.blockPlace::remove);
    }
}
