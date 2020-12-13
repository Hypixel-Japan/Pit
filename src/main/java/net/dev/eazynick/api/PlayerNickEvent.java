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
        return new HandlerList();
    }

    public HandlerList getHandlerList()
    {
        return new HandlerList();
    }

    public Player getPlayer() {
        return null;
    }
}
