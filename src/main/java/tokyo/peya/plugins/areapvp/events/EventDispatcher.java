package tokyo.peya.plugins.areapvp.events;

import org.bukkit.*;
import org.bukkit.event.*;

public class EventDispatcher
{
    public static <T extends Event> T dispatch(T event)
    {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
