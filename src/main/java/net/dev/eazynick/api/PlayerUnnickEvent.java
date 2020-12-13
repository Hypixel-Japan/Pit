package net.dev.eazynick.api;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class PlayerUnnickEvent extends Event implements Cancellable
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
    public Player getPlayer() {
        return null;
    }

    @Override
    public HandlerList getHandlers()
    {
        return new HandlerList();
    }

    public HandlerList getHandlerList()
    {
        return new HandlerList();
    }
}
