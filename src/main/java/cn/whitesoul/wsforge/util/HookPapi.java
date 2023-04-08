package cn.whitesoul.wsforge.util;

import cn.whitesoul.wsforge.data.Cache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

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
            return Cache.names.get(player.getUniqueId());
        }
        if(params.equalsIgnoreCase("level")) {
            return String.valueOf(Cache.level.get(player.getUniqueId()));
        }
        if(params.equalsIgnoreCase("exp")) {
            return String.valueOf(Cache.exp.get(player.getUniqueId()));
        }
        return null;
    }

}
