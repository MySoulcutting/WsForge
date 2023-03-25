package cn.whitesoul.wsforge.command;

import cn.whitesoul.wsforge.gui.ForgeGUI;
import cn.whitesoul.wslib.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;
        if (args.length == 0) {
            Message.sendMessage(player,"§6§l指令帮助:");
            Message.sendMessage(player, "§6§l/wsforge open [玩家名] &b打开玩家锻造台");
            Message.sendMessage(player, "&a&l/wsforge reload &b重载配置文件");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
               Player target = Bukkit.getPlayer(args[1]);
                ForgeGUI.open(target);
                Message.sendMessage(player, "&a&l你打开了锻造台");
            }
        return false;
    }
}
