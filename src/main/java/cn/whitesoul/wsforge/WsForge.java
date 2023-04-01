package cn.whitesoul.wsforge;

import cn.whitesoul.wsforge.command.MainCommand;
import cn.whitesoul.wsforge.listener.InventoryListener;
import cn.whitesoul.wsforge.listener.PlayerListener;
import cn.whitesoul.wsforge.util.HookPapi;
import cn.whitesoul.wslib.database.mysql.Mysql;
import cn.whitesoul.wslib.database.mysql.SQL;
import cn.whitesoul.wslib.file.WsYaml;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WsForge extends JavaPlugin {
    public static WsForge instance;
    public static WsYaml forge;
    private final String mysqlUrl = this.getConfig().getString("SQL.Url");
    private final String mysqlDatabase = this.getConfig().getString("SQL.Database");
    private final String mysqlName = this.getConfig().getString("SQL.Name");
    private final String mysqlPassword = this.getConfig().getString("SQL.Password");

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HookPapi().register();
        }
        //数据库初始化
        Mysql.setConn(mysqlUrl, mysqlDatabase, mysqlName, mysqlPassword);
        SQL.createTable("wsforge_table","id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                "UUID VARCHAR(255) NULL, " +
                "PLAYER VARCHAR(255) NULL, " +
                "LEVEL INT(11) NOT NULL, " +
                "EXP INT(11) NULL, " +
                "NAME LONGTEXT NULL, " +
                "PRIMARY KEY ( id )");
        instance = this;
        saveDefaultConfig();
        forge = new WsYaml(this, "forge.yml");
        sendInfo("&f[白魂锻造] &a启动成功");
        sendInfo("&f[白魂锻造] &a作者:白魂");
        sendInfo("&f[白魂锻造] &a版本:"+getDescription().getVersion());
        getCommand("wsforge").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
    }

    @Override
    public void onDisable() {

    }
    public static void sendInfo(String info) {
        WsForge.instance.getLogger().info(info.replace("&", "§"));
    }
}
