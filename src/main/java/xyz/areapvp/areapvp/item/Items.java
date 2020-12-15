package xyz.areapvp.areapvp.item;

import xyz.areapvp.areapvp.item.items.*;

import java.util.*;

public class Items
{
    public static ArrayList<IShopItem> items = new ArrayList<>();

    public static void addItem(IShopItem iShopItem)
    {
        items.add(iShopItem);
    }

    public static IShopItem getItem(String name)
    {
        for (IShopItem item : items)
            if (item.getName().equals(name))
                return item;
        return null;
    }

    public static void newLine()
    {
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
        Items.addItem(new ItemAir());
    }

}
