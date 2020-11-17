package xyz.areapvp.areapvp;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class KillStreak
{
    private static final HashMap<UUID, Long> st = new HashMap<>();

    public static Long getStreak(UUID player)
    {
        Long str = st.get(player);
        return str == null ? 0: str;
    }

    public static void kill(Player player)
    {
        st.merge(player.getUniqueId(), 1L, Long::sum);
    }

    public static void reset(Player player)
    {
        st.remove(player.getUniqueId());
    }
}
