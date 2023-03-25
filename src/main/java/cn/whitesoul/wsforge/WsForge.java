package cn.whitesoul.wsforge;

import cn.whitesoul.wsforge.command.MainCommand;
import cn.whitesoul.wsforge.listener.InventoryListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class WsForge extends JavaPlugin {
    public static WsForge instance;

    @Override
    public void onEnable() {
        instance = this;
        sendInfo("&f[白魂锻造] &a启动成功");
        sendInfo("&f[白魂锻造] &a作者:白魂");
        sendInfo("&f[白魂锻造] &a版本:"+getDescription().getVersion());
        getCommand("wsforge").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {

    }
    public static void sendInfo(String info) {
        WsForge.instance.getLogger().info(info.replace("&", "§"));
    }
}
