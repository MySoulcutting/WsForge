package cn.whitesoul.wsforge.listener;

import cn.whitesoul.wsforge.WsForge;
import cn.whitesoul.wslib.database.mysql.Mysql;
import cn.whitesoul.wslib.database.mysql.SQL;
import cn.whitesoul.wslib.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import cn.whitesoul.wsforge.data.Cache;
import org.bukkit.event.player.PlayerQuitEvent;

import static cn.whitesoul.wsforge.WsForge.*;

public class PlayerListener implements Listener {
    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) throws SQLException {
        //初始化玩家数据
        UUID uuid = event.getPlayer().getUniqueId();
        String player = event.getPlayer().getName();
        //初始化数据库
        PreparedStatement preparedStatement = null;
        ResultSet rs = SQL.uuidQuery(uuid, "wsforge_table");
        if (!rs.next()) {
            preparedStatement = SQL.insertTableSQL("wsforge_table", "ID, UUID, PLAYER, LEVEL, EXP,NAME", "?,?,?,?,?,?");
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(3, player);
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, 0);
            preparedStatement.setString(6, "test");
            preparedStatement.executeUpdate();
        }
        //写入HashMap
        String level = "SELECT UUID, LEVEL, EXP, NAME FROM wsforge_table WHERE UUID = ?";
        preparedStatement = Mysql.getConn().prepareStatement(level);
        preparedStatement.setString(1, uuid.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int levels = resultSet.getInt("LEVEL");
            int exp = resultSet.getInt("EXP");
            String name = resultSet.getString("NAME");
            Cache.exp.put(uuid, exp);
            Cache.level.put(uuid, levels);
            Cache.names.put(uuid, name);
            //TEST
            xConomyAPI.changePlayerBalance(uuid, event.getPlayer().getDisplayName(), xConomyAPI.formatdouble("50"),true);
            Message.sendMessage(event.getPlayer(), "§a欢迎回来！你的等级是" + Cache.level.get(uuid) + "级，经验值是" + Cache.exp.get(uuid) + "点，你的名字是" + Cache.names.get(uuid));
        }
    }
    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        //更新数据库
        SQL.updateUUID(uuid, "wsforge_table", "LEVEL", Cache.level.get(uuid).toString());
        SQL.updateUUID(uuid, "wsforge_table", "EXP", Cache.exp.get(uuid).toString());
        SQL.updateUUID(uuid, "wsforge_table", "NAME", Cache.names.get(uuid));
        Cache.exp.remove(uuid);
        Cache.level.remove(uuid);
        Cache.names.remove(uuid);
    }
    }
