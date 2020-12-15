package xyz.areapvp.areapvp.play.decoration;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.*;
import org.bukkit.entity.*;

public class Sounds
{
    public static void play(Player player, String name, float pitch)
    {
        SoundEffect effect;
        if ((effect = SoundEffect.a.get(new MinecraftKey(name))) == null)
            return;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(
                effect,
                SoundCategory.PLAYERS,
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                0.5f,
                pitch
        ));
    }

    public static void play(Player player, SoundType type)
    {
        play(player, type.getName(), type.getPitch());
    }

    public enum SoundType
    {
        KILL("entity.experience_orb.pickup", 1.8f),
        ARROW_HIT("entity.experience_orb.pickup", 0f);

        final String name;
        final float pitch;

        SoundType(String name, float pitch)
        {
            this.name = name;
            this.pitch = pitch;
        }

        public float getPitch()
        {
            return pitch;
        }

        public String getName()
        {
            return name;
        }
    }
}
