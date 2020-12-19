package tokyo.peya.plugins.areapvp.api;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.*;
import tokyo.peya.plugins.areapvp.*;

import javax.annotation.*;

public class SimplePitAPI
{
    public static void changeBlockRemoveTimer(Location blockAt, @Nullable Integer sec, @Nullable Material replaced)
    {
        if (blockAt == null)
            throw new NullPointerException("Parameter [blockAt] is cannot be null!");
        if (sec != null && replaced != null)
        {
            AreaPvP.block.put(blockAt, new Tuple<>(sec, replaced));
            return;
        }

        if (AreaPvP.block.get(blockAt) == null)
            return;

        AreaPvP.block.put(blockAt, new Tuple<>(sec, replaced));
    }

}
