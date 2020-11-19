package xyz.areapvp.areapvp.level;

import org.bukkit.entity.Player;
import xyz.areapvp.areapvp.Kill;

public class Exp
{
    public static long calcKillExp(Player killer, Player death, int level, int prestige)
    {
        int base = 10;
        if (Kill.getStreak(killer.getUniqueId()) >= 10)
            base += 10;
        if (Kill.getStreak(death.getUniqueId()) >= 10)
            base += 10;
        if (prestige == 0)
            return base;
        base += (prestige * 1.25);
        return base;
    }


    public static long getExp(int level, int prestige )
    {
        if (level == 120)
            return 0;
        if (prestige == 0)
            return ab(level);
        return ab(level) * (prestige * 110 / 100);
    }
    private static int ab(int level)
    {
        int real = 1;
        if (level < 10)
            real = 15;
        else if (level < 20)
            real = 30;
        else if (level < 30)
            real = 50;
        else if (level < 40)
            real = 75;
        else if (level < 50)
            real = 125;
        else if (level < 60)
            real = 300;
        else if (level < 70)
            real = 600;
        else if (level < 80)
            real = 800;
        else if (level < 90)
            real = 900;
        else if (level < 100)
            real  = 1000;
        else if (level < 110)
            real = 1200;
        else if (level < 120)
            real = 1500;
        else if (level == 120)
            real = 0;
        return real + (level * 2);
    }
}
