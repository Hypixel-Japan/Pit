package xyz.areapvp.areapvp.player;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import xyz.areapvp.areapvp.*;
import xyz.areapvp.areapvp.perk.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class PlayerModify
{
    public static boolean isCreated(Player player)
    {
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT UUID FROm player WHERE UUID=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            return statement.executeQuery().next();
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
             PreparedStatement wh = connection.prepareStatement("SELECT PERK from perk WHERE UUID=?");
             PreparedStatement ow = connection.prepareStatement("SELECT PERK from holdperk WHERE UUID=?"))

        {
            statement.setString(1, uuid.toString().replace("-", ""));
            wh.setString(1, uuid.toString().replace("-", ""));
            ow.setString(1, uuid.toString().replace("-", ""));

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

            ArrayList<String> ownPerkStr = new ArrayList<>();
            ResultSet own = ow.executeQuery();
            while (own.next())
                ownPerkStr.add(own.getString("PERK"));
            return new PlayerInfo(level, exp, prestige, perkStr, ownPerkStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void addLevel(Player player, int level, long exp)
    {
        if (!InfoContainer.isInitialize(player))
            return;
        PlayerInfo info = InfoContainer.getInfo(player);

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player SET LEVEL=?, EXP=? WHERE UUID=?"))
        {
            statement.setInt(1, Math.min(info.level + level, 120));
            if (level == 0)
                statement.setLong(2, info.exp + exp);
            else
                statement.setLong(2, exp);
            statement.setString(3, player.getUniqueId().toString().replace("-", ""));
            statement.execute();
        }
        catch (Exception ignored)
        {
        }
        PlayerModify.setMetaData(player, "AreaPvP.Exp", info.exp + exp);

        if (level == 0)
            return;
        PlayerModify.setMetaData(player, "AreaPvP.Level", Math.min(info.level + level, 120));

        AreaPvP.refreshScoreBoard(player);
        player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "LEVEL UP!",
                PlayerInfo.getPrefix(info.level, info.prestige) + ChatColor.GRAY + " → " +
                        PlayerInfo.getPrefix(Math.min(info.level + level, 120), info.prestige),
                10, 20, 10
        );
    }

    public static void removeOwnPerk(Player player, String perk)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (!info.perk.contains(perk))
            return;

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM holdperk WHERE UUID=? AND PERK=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.setString(2, perk);
            statement.execute();
            Perk.update(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addOwnPerk(Player player, String perk)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (info.ownPerk.contains(perk))
            return;
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO holdperk VALUES (?, ?)"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.setString(2, perk);
            statement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addPerk(Player player, String perk)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (info.perk.contains(perk))
            return;

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO perk VALUES (?, ?)"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.setString(2, perk);
            statement.execute();
            Perk.update(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void removePerk(Player player, String perk)
    {
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (!perk.equals("*") && !info.perk.contains(perk))
            return;

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM perk WHERE UUID=? AND PERK=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.setString(2, perk);
            statement.execute();
            Perk.update(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void clearOwnPerk(Player player)
    {
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM holdperk WHERE UUID=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void clearPerk(Player player)
    {
        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM perk WHERE UUID=?"))
        {
            statement.setString(1, player.getUniqueId().toString().replace("-", ""));
            statement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addPrestige(Player player)
    {
        if (!InfoContainer.isInitialize(player))
            return;
        PlayerInfo info = InfoContainer.getInfo(player);

        if (info.level < 120)
            return;

        try (Connection connection = AreaPvP.data.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player SET LEVEL=?, PRESTIGE=?, EXP=? WHERE UUID=?"))
        {
            statement.setString(4, player.getUniqueId().toString().replace("-", ""));


            statement.setInt(1, 1);
            statement.setInt(2, info.prestige + 1);
            statement.setInt(3, 0);
            statement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        clearOwnPerk(player);
        clearPerk(player);
        AreaPvP.economy.withdrawPlayer(player, AreaPvP.economy.getBalance(player));

        InfoContainer.initialize(player);
        AreaPvP.refreshScoreBoard(player);

        player.sendTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "PRESTIGE!",
                ChatColor.GRAY + "あなたはprestige " +
                        ChatColor.YELLOW + PlayerInfo.arabicToRoman(info.prestige + 1) +
                        ChatColor.GREEN + " をアンロックしました！",
                10, 20, 10
        );
        Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "PRESTIGE! " +
                ChatColor.RESET + ChatColor.GRAY + player.getName() + "さんが、prestige " +
                ChatColor.YELLOW + PlayerInfo.arabicToRoman(info.prestige + 1) +
                ChatColor.GRAY + " をアンロックしました。gg！");
    }

    public static void addExp(Player player, long exp)
    {
        if (exp == 0L)
            return;

        if (!InfoContainer.isInitialize(player))
            return;
        PlayerInfo info = InfoContainer.getInfo(player);

        if (info.level == 120)
            return;

        long next = Exp.getExp(info.level + 1, info.prestige);
        Debugger.debug(player, "Next: " + next);
        if (next - (info.exp + exp) > 0)
        {
            Debugger.debug(player, "Not LevelUP");
            Debugger.debug(player, "ADD EXP: " + exp);
            addLevel(player, 0, exp);
            return;
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
        addLevel(player, levelUp - 1, ex);
        PlayerModify.setMetaData(player, "AreaPvP.Exp", ex);

    }

    public static Optional<MetadataValue> getMetaData(Entity entity, String key)
    {
        AtomicReference<Optional<MetadataValue>> val = new AtomicReference<>(Optional.empty());
        entity.getMetadata(key)
                .parallelStream()
                .forEach(value -> {
                    if (value.getOwningPlugin().getName().equals(AreaPvP.getPlugin().getName()))
                        val.set(Optional.of(value));
                });
        return val.get();
    }

    public static void setMetaData(Entity entity, String key, Object value)
    {
        entity.setMetadata(key, new FixedMetadataValue(AreaPvP.getPlugin(), value));
    }

    public static void removeMetaData(Entity entity, String key)
    {
        entity.removeMetadata(key, AreaPvP.getPlugin());
    }
}
