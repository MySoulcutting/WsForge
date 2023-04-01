package cn.whitesoul.wsforge.util;

import cn.whitesoul.wsforge.data.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

public class HookPapi extends PlaceholderExpansion {

    @Override
    public  String getIdentifier() {
        return "wsforge";
    }

    @Override
    public  String getAuthor() {
        return "white_soul";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "null";
        }
        if(params.equalsIgnoreCase("name")) {
            return Map.names.get(player.getUniqueId());
        }
        if(params.equalsIgnoreCase("level")) {
            return String.valueOf(Map.level.get(player.getUniqueId()));
        }
        if(params.equalsIgnoreCase("exp")) {
            return String.valueOf(Map.exp.get(player.getUniqueId()));
        }
        return null;
    }

}
