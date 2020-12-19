package tokyo.peya.plugins.areapvp.api.events;

import tokyo.peya.plugins.areapvp.events.*;

public class PlayerSpawnEvent extends EventBase
{
    private String cancelMessage = null;

    public void setCancelMessage(String cancelMessage)
    {
        this.cancelMessage = cancelMessage;
    }

    public String getCancelMessage()
    {
        return cancelMessage;
    }
}
