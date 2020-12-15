package xyz.areapvp.areapvp;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.metadata.*;
import org.bukkit.scheduler.*;

import java.util.*;

public class Timer extends BukkitRunnable
{
    @Override
    public void run()
    {
        blockBreak();
        hit();
    }

    private void hit()
    {
        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> {

                    if (player.hasMetadata("x-spawn"))
                    {
                        Integer spawn = null;
                        for (MetadataValue hitter : player.getMetadata("x-spawn"))
                            if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                                spawn = hitter.asInt();

                        if (spawn != null)
                        {
                            spawn--;
                            player.removeMetadata("x-spawn", AreaPvP.getPlugin());

                            if (spawn > 0)
                                player.setMetadata("x-spawn", new FixedMetadataValue(AreaPvP.getPlugin(), spawn));

                        }
                    }

                    if (!player.hasMetadata("x-hitted"))
                    {
                        player.removeMetadata("x-hitter", AreaPvP.getPlugin());
                        return;
                    }

                    int hitted = 15;
                    for (MetadataValue hitter : player.getMetadata("x-hitted"))
                        if (hitter.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                            hitted = hitter.asInt();
                    hitted = hitted - 1;
                    player.removeMetadata("x-hitted", AreaPvP.getPlugin());
                    if (hitted > 0)
                        player.setMetadata("x-hitted", new FixedMetadataValue(AreaPvP.getPlugin(), hitted));
                });
    }

    private void blockBreak()
    {
        ArrayList<Location> removeKeys = new ArrayList<>();
        AreaPvP.block
                .forEach((tr, v) -> {
                    if (v.a() == null)
                        return;

                    int val = v.a() - 1;
                    if (val > 0)
                    {
                        AreaPvP.block.put(tr, new Tuple<>(val, v.b()));
                        return;
                    }

                    removeKeys.add(tr);
                    tr.getWorld().getBlockAt(tr).removeMetadata("placed", AreaPvP.getPlugin());
                    tr.getWorld().getBlockAt(tr).setType(v.b());
                });

        removeKeys.parallelStream().forEach(AreaPvP.block::remove);

    }
}
