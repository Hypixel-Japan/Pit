package kernitus.plugin.OldCombatMechanics.utilities.damage;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class OCMEntityDamageByEntityEvent extends Event
{ //Exclude in Build

    public void setBaseDamage(double baseDamage)
    {
    }

    @Override
    public HandlerList getHandlers()
    {
        return null;
    }

    public Entity getDamager()
    {
        return null;
    }

}
