package xyz.areapvp.areapvp.level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import xyz.areapvp.areapvp.AreaPvP;
import xyz.areapvp.areapvp.perk.Perk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

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
        if (level == 0)
            return;
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
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;
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

        player.sendTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "PRESTIGE!",
                ChatColor.GRAY + "あなたはprestige " +
                        ChatColor.YELLOW + PlayerInfo.arabicToRoman(info.prestige + 1) +
                        ChatColor.GREEN + " をアンロックしました！",
                10, 20, 10
        );
    }

    public static void addExp(Player player, long exp)
    {
        if (exp == 0L)
            return;
        PlayerInfo info = getInfo(player);
        if (info == null)
            return;

        if (info.level == 120)
            return;

        long xp = info.exp + exp;
        int prestige = info.prestige;
        long level = info.level;

        long nextWheat = 1L;
        long next = Exp.getExp(Math.toIntExact(level + nextWheat), prestige);
        if (Exp.getExp(Math.toIntExact(level), prestige) >= xp)
        {
            addLevel(player, 0, exp);
            return;
        }
        while (next < exp)
            next += Exp.getExp(Math.toIntExact(level + ++nextWheat), prestige);
        addLevel(player, Math.toIntExact(nextWheat), xp - Exp.getExp(Math.toIntExact(level + nextWheat), prestige));
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
