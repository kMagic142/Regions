package ro.kmagic.regions.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> colorList(List<String> list) {
        List<String> newList = new ArrayList<>();

        for(String str : list) {
            newList.add(color(str));
        }

        return newList;
    }

}
