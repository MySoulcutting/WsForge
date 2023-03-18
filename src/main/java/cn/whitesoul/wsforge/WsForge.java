package cn.whitesoul.wsforge;

import cn.whitesoul.wslib.message.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class WsForge extends JavaPlugin {

    @Override
    public void onEnable() {
        ServerInfo.sendInfo("&f[白魂锻造] &a启动成功");
        ServerInfo.sendInfo("&f[白魂锻造] &a作者:白魂");
        ServerInfo.sendInfo("&f[白魂锻造] &a版本:"+getDescription().getVersion());
    }

    @Override
    public void onDisable() {

    }
}
