package net.dev.eazynick.api;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class PlayerNickEvent extends Event implements Cancellable
{

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel)
    {

    }

    public HandlerList getHandlers()
    {
        return null;
    }

    public HandlerList getHandlerList()
    {
        return null;
    }

    public Player getPlayer() {
        return null;
    }
}
