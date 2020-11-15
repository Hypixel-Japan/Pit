package xyz.areapvp.areapvp;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable
{
    @Override
    public void run()
    {
        breakBlock();
    }

    private void breakBlock()
    {
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

                    AreaPvP.blockPlace.remove(key);
                    key.getWorld().getBlockAt(key).removeMetadata("newPlayer", AreaPvP.getPlugin());
                    key.getWorld().getBlockAt(key).setType(Material.AIR);
                });
    }
}
