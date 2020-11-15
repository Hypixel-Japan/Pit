package xyz.areapvp.areapvp.level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.PlayerEditor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerModify
{
    public static boolean isCreated(Player player)
    {
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT UUID FROm player WHERE UUID=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            return  statement.executeQuery().next();
        }
        catch (Exception ignored)
        {
            return false;
        }
    }
    public static void createBalance(Player player)
    {
        createBalance(player, false);
    }

    public static void createBalance(Player player, boolean tested)
    {
        if (tested && isCreated(player))
            return;
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO player VALUES (" +
                     "?," +
                     "1," +
                     "0," +
                     "0)"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.execute();
        }
        catch (Exception ignored)
        {
        }
    }

    public static PlayerInfo getInfo(Player player)
    {
        UUID uuid = player.getUniqueId();
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM player WHERE UUID=?");
             PreparedStatement wh = connection.prepareStatement("SELECT perk from perk WHERE UUID=?"))

        {
            statement.setString(1, uuid.toString().replace("-", ""));
            wh.setString(1, uuid.toString().replace("-", ""));

            ResultSet data = statement.executeQuery();
            if (!data.next())
                return null;

            int level = data.getInt("LEVEl");
            int prestige = data.getInt("PRESTIGE");
            long exp = data.getLong("EXP");

            data.close();

            ResultSet perks = wh.executeQuery();

            ArrayList<String> perkStr = new ArrayList<>();

            while (perks.next())
                perkStr.add(perks.getString("PERK"));

            return new PlayerInfo(level, exp, prestige, perkStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void addLevel(Player player, int level, long exp)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player SET LEVEL=?, EXP=? WHERE UUID=?"))
        {
            statement.setInt(1, info.level + level);
            statement.setLong(2, exp);
            statement.setString(3, player.getUniqueId().toString().replace("-", ""));
            statement.execute();
        }
        catch (Exception ignored) { }
        player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "LEVEL UP!",
                PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " → " +
                        PlayerInfo.getPrefix(info.level + level, info.prestige),
                10, 20, 10);
        PlayerEditor.changePlayerHead(player, info.prestige, info.level + level, PlayerEditor.Type.UPDATE);
    }

    public static void addExp(Player player, long exp)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (info.level == 120)
            return;

        long xp = info.exp + exp;
        int prestige = info.prestige;
        int level = info.level;

        int add = 1;

        while (true)
        {
            long a = Exp.getExp(level + add, prestige);
            if(a > xp)
            {
                int l = add - 1;
                if (l == 0)
                    break;
                addLevel(player, l, xp);
                break;
            }
            xp = xp - a;
            add++;
        }
    }
}
