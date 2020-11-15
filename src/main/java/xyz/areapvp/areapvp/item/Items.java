package xyz.areapvp.areapvp.item;

import java.util.ArrayList;

public class Items
{
    public static ArrayList<IShopItem> items = new ArrayList<>();
    public static void addItem(IShopItem iShopItem)
    {
        items.add(iShopItem);
    }

    public static IShopItem getItem(String name)
    {
        for (IShopItem item: items)
            if (item.getName().equals(name))
                return item;
        return null;
    }
}
