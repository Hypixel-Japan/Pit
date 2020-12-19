package tokyo.peya.plugins.areapvp.player;

import org.apache.commons.lang3.tuple.*;
import org.bukkit.entity.*;
import tokyo.peya.plugins.areapvp.*;
import tokyo.peya.plugins.areapvp.play.*;

public class Exp
{
    public static Pair<Integer, Long> calcLevelUpAmountAndSurplusExp(PlayerInfo info, long exp, Player player)
    {
        if (info.level == 120)
            return Pair.of(0, 0L);

        long next = Exp.getExp(info.level + 1, info.prestige);
        Debugger.debug(player, "Next: " + next);
        if (next - (info.exp + exp) > 0)
        {
            Debugger.debug(player, "Not LevelUP");
            Debugger.debug(player, "ADD EXP: " + exp);
            return Pair.of(0, exp);
        }
        Debugger.debug(player, "LevelUP!!!");

        int levelUp = 1;

        long ex = info.exp + exp;


        while (Exp.getExp(info.level + levelUp, info.prestige) <= ex)
        {
            Debugger.debug(player, "UP: " + levelUp + ", EXP: " + ex + ", REQ: " + Exp.getExp(info.level + levelUp, info.prestige));
            ex -= Exp.getExp(info.level + levelUp, info.prestige);
            levelUp++;
            if ((info.level + levelUp) > 120)
            {
                Debugger.debug(player, "!!!PRESTIGE!!!");
                ex = 0;
                break;
            }
        }
        Debugger.debug(player, "Level UP: " + (levelUp - 1));
        Debugger.debug(player, "ADD EXP: " + ex);
        Debugger.debug(player, "Next Require: " + Exp.getExp(info.level + levelUp, info.prestige));
        return Pair.of(levelUp - 1, ex);
    }

    public static long calcKillExp(Player killer, Player death, int level, int prestige)
    {
        int base = 10;
        if (Kill.getStreak(killer.getUniqueId()) >= 10)
            base += 10;
        if (Kill.getStreak(death.getUniqueId()) >= 10)
            base += 10;
        if (prestige == 0)
            return base;
        base += (int) (prestige * 1.25);
        return base;
    }

    public static long getExp(int level, int prestige)
    {
        if (level == 120)
            return 0;
        if (prestige == 0)
            return ab(level);
        return ab(level) * (prestige * 110L / 100);
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
            real = 1000;
        else if (level < 110)
            real = 1200;
        else if (level < 120)
            real = 1500;
        else if (level == 120)
            real = 0;
        return real + (level * 2);
    }
}
