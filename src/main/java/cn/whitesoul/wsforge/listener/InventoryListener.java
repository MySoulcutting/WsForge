package cn.whitesoul.wsforge.listener;

import cn.whitesoul.wsforge.WsForge;
import cn.whitesoul.wsforge.data.Cache;
import cn.whitesoul.wsforge.gui.ForgeGUI;
import cn.whitesoul.wslib.message.Message;
import cn.whitesoul.wslib.util.WsVault;
import com.sun.istack.internal.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yic.xconomy.data.syncdata.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.whitesoul.wsforge.gui.ForgeGUI.title;
import static org.bukkit.Bukkit.getServer;

public class InventoryListener implements Listener {
    private static final FileConfiguration config = WsForge.forge.getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (event.getClickedInventory() != null && event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains(title)) {
            int[] notSlot = new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26};
            for (int i : notSlot) {
                if (event.getSlot() == i) {
                    event.setCancelled(true);
                }
            }
            //点击锻造按钮
            if (event.getSlot() == 22) {
                if (event.getInventory().firstEmpty() == -1) {
                    Message.sendMessage(player, "&c&l你的背包已满,无法锻造");
                    return;
                }
                // 判断13格是否有图纸
                if (event.getClickedInventory() != null && event.getClickedInventory().getItem(13) != null && event.getClickedInventory().getItem(13).getType() != Material.AIR) {
                    if (event.getClickedInventory().getItem(13).getAmount() == 1) {
                        // 锻造过程
                        draw(event.getClickedInventory().getItem(13), player);
                    } else {
                        player.closeInventory();
                        Message.sendMessage(player, "你只能放一张图纸在这里！");
                    }
                } else {
                    player.closeInventory();
                    Message.sendMessage(player, "你没有放图纸在这里！");
                }
            }
        }
    }
    //锻造过程
    public static void draw(ItemStack itemStack, Player player) {
        //判断图纸所需等级
        if (WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needLevel") > Cache.level.get(player.getUniqueId())) {
            Message.sendMessage(player, "&c&l你的等级不足,无法锻造");
            player.closeInventory();
            return;
        }
        //判断图纸所需金钱
        PlayerData playerData = WsForge.xConomyAPI.getPlayerData(player.getUniqueId());
        if (playerData.getBalance().intValue() >= WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needMoney")) {
            //扣除金币
            WsForge.xConomyAPI.changePlayerBalance(player.getUniqueId(),player.getDisplayName(), BigDecimal.valueOf(WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needMoney")),false);
            //锻造物品
            ItemStack drawItem = ItemManager.INSTANCE.getItemStack(config.getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".item"), player);
            player.getOpenInventory().setItem(4, drawItem);
            //锻造执行指令
            List list = WsForge.forge.getConfig().getList("DrawName." + itemStack.getItemMeta().getDisplayName() + ".commands");
            for (Iterator it2 = list.iterator(); it2.hasNext(); ) {
                String[] s = it2.next().toString().split("#");
                String cmd = PlaceholderAPI.setPlaceholders(player, s[1].replace("&", "§"));
                switch (s[0]) {
                    case "op":
                        boolean isOp = player.isOp();
                        try {
                            player.setOp(true);
                            Bukkit.getServer().dispatchCommand(player, cmd);
                        } finally {
                            player.setOp(isOp);
                        }
                        break;
                    case "player":
                        getServer().dispatchCommand(player, cmd);
                        break;
                    case "console":
                        getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        break;
                }
            }
            //发送全服消息
            if (WsForge.forge.getConfig().getBoolean("DrawName." + itemStack.getItemMeta().getDisplayName() + ".broadcast")) {
                Message.sendAllPlayerMessage(WsForge.forge.getConfig().getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".broadcastMessage").replace("{player}",player.getDisplayName()).replace("{item}", drawItem.getItemMeta().getDisplayName().toString()));
            }
            //给予经验
            Random random = new Random();
            String[] s = WsForge.forge.getConfig().getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".giveExp").split("-");
            int max = Integer.parseInt(s[1]);
            int min = Integer.parseInt(s[0]);
            int exp = random.nextInt(max - min + 1) + min;
            Cache.exp.put(player.getUniqueId(), Cache.exp.get(player.getUniqueId()) + exp);
            //删除图纸
            player.getOpenInventory().setItem(13, null);
            //锻造信息
            Message.sendMessage(player, WsForge.INSTANCE.getConfig().getString("Message.success").replace("&", "§").replace("{item}", drawItem.getItemMeta().getDisplayName().toString()).replace("{exp}", String.valueOf(exp)));
            TextComponent baseComponent = new TextComponent("test");
            ComponentBuilder componentBuilder = new ComponentBuilder("§a§l锻造物品信息:\n");
            //锻造时间
            componentBuilder.append("§a锻造时间: §e§l"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n");
            componentBuilder.append("§a物品名:" + drawItem.getItemMeta().getDisplayName()+"\n"+"§a物品介绍:\n");
            for (String i : drawItem.getItemMeta().getLore()){
                componentBuilder.append(i+"\n");
            }
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,componentBuilder.create());
            baseComponent.setHoverEvent(hoverEvent);
            player.spigot().sendMessage(baseComponent);
        } else {
            Message.sendMessage(player, "&c&l你的金钱不足,无法锻造");
            player.closeInventory();
        }
    }
    //关闭
    @EventHandler
    public void InventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle() != null && event.getInventory().getTitle().contains(title)) {
            if (event.getInventory().getItem(13) != null) {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
            }
            if (event.getInventory().getItem(4) != null) {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(4));
            }
        }
    }

}
