package xyz.areapvp.areapvp.perk.perks;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.areapvp.areapvp.perk.IPerkEntry;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public class GoldenHead implements IPerkEntry
{
    final String data = "ewogICJ0aW1lc3RhbXAiIDogMTYwNjE0NDk4ODIyNSwKICAicHJvZmlsZUlkIiA6ICIwMWIwNWI4MDIyNWU0NDcyYTEzZGU2ODc5YjU2NGEwOSIsCiAgInByb2ZpbGVOYW1lIiA6ICIwMFA1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y4MDY0MWY4ODIzZDgzODQ3NGVkMjcyYmVhOGJiNzA5YzE1MTQ5YWU5YWFjYjIzOGQ4NmE1MTU1Y2MyNDUwYWYiCiAgICB9CiAgfQp9";
    final String signature = "bXE2eYztT00At+1IzTO/76C4zrtx78KWJe+u2exy+opFn1IgL+mFNC6jzhB6S85jzrikW1RGQTGdroxDFF6pLxxBXJ8xLLnyu9TguHHDfFvUlW0t9BABoXsbeOhHAh7a23ns+a7ZD3d/X500wCkEhyb8Lw6k5tKodY/b8FjbfRXXhzwQfzHclRf038ek4WBF+thPClfFo5jKFNsLsf+xBMn9FgXVDXJH7UmkDb7B16w2aE7ICugYvrrQjY035qpTV7otmbrMLktc7iGAixXKShpRCU5dV5nW/BdhK3n4Y6+HL0yAam8rm+E0cRsAtppeFaHPzPmwtnaPZUo9dfpdPBYjlStzJdHTtiMBrgCvRyfU49c9UrbH2z71YzKyMVuldK3AtQjDE66Qw9zvggHnMA+XhE5jNsb53C9912Hh8TegcCXH0risYQM6MbMd1q6dUqP7vzWZHr4GLzdc8LQEU510uGl+YN4i6UCx/ohOTL/pNYNvgs/+KFocndmDfRwxtfZK6OHw9EOREpxyTW6IhP1ue4CuFYXRTh933pisLN87EVMzVAapZ8/MhW9mamzGnZ4aPKXkH78zE3ykUzj0d8DCYTFcKxA1DqgcLyhNoaQW8Gq+dssbRitHcc9lqKPC6dxTjCa0gDUiMYsaOvBkOcO11ShQ7nU0jCy/2vZpr2I=";

    @Override
    public ItemStack getItem()
    {
        GameProfile profile = new GameProfile(UUID.fromString("e71d6198-4015-459e-b3e3-8192c0d19360"), "GoldenHead");
        profile.getProperties().put("textures", new Property("textures", data, signature));
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();

        try
        {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        }
        catch (Exception ignored)
        {
            return stack;
        }

        meta.setDisplayName(ChatColor.GOLD + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.BLUE + "Speed I (0:08)",
                ChatColor.BLUE + "Regeneration II (0:05)",
                ChatColor.GOLD + "3❤ absorption!"));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public String getName()
    {
        return "gHead";
    }

    @Override
    public int getNeedPrestige()
    {
        return 0;
    }

    @Override
    public int getNeedGold()
    {
        return 500;
    }

    @Override
    public void onBuy(Player player)
    {

    }

    @Override
    public void onRemove()
    {

    }

    @Override
    public void onWork(Player player)
    {
        if (player.hasPotionEffect(PotionEffectType.SPEED))
            player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 180, 1, false));
        if (player.hasPotionEffect(PotionEffectType.REGENERATION))
            player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1, false));

        if (player.hasPotionEffect(PotionEffectType.ABSORPTION))
        {
            player.removePotionEffect(PotionEffectType.ABSORPTION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,114514, 2, false));

        }
        else
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,114514,  1, false));
            player.damage(2);
        }
    }
}
