package xyz.areapvp.areapvp.perk;

import java.util.ArrayList;

public class Perks
{
    public static ArrayList<IPerkEntry> perks = new ArrayList<>();
    public static void addPerk(IPerkEntry iPerkEntry)
    {
        perks.add(iPerkEntry);
    }

    public static IPerkEntry getPerk(String name)
    {
        for (IPerkEntry item: perks)
            if (item.getName().equals(name))
                return item;
        return null;
    }
}
