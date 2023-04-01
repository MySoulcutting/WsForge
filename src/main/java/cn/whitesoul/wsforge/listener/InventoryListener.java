package cn.whitesoul.wsforge.listener;

import cn.whitesoul.wsforge.WsForge;
import cn.whitesoul.wsforge.data.Map;
import cn.whitesoul.wslib.message.Message;
import cn.whitesoul.wslib.util.WsVault;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.item.ItemPack;
import pers.neige.neigeitems.manager.ItemManager;
import pers.neige.neigeitems.manager.ItemPackManager;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class InventoryListener implements Listener {
    private static final FileConfiguration config = WsForge.forge.getConfig();

    @EventHandler
    @NotNull
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory().getTitle().equalsIgnoreCase("锻造台") && event.getClickedInventory() != null) {
            //禁止点击
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

    @EventHandler
    public void InventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().equalsIgnoreCase("锻造台")) {
            if (event.getInventory().getItem(13) != null) {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
            }
            if (event.getInventory().getItem(4) != null) {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(4));
            }
        }
    }

    //锻造过程
    public static void draw(ItemStack itemStack, Player player) {
        //判断图纸所需等级
        if (WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needLevel") > Map.level.get(player.getUniqueId())) {
            Message.sendMessage(player, "&c&l你的等级不足,无法锻造");
            player.closeInventory();
            return;
        }
        //判断图纸所需金钱
        if (WsVault.hasEconomy(player, WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needMoney"))) {
            //扣除金币
            WsVault.removeEconomy(player, WsForge.forge.getConfig().getInt("DrawName." + itemStack.getItemMeta().getDisplayName() + ".needMoney"));
            //锻造物品
            ItemStack drawItem = ItemManager.INSTANCE.getItemStack(config.getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".item"), player);
            player.getOpenInventory().setItem(4, drawItem);
            //锻造执行指令
            CommandSender op = (CommandSender) Proxy.newProxyInstance(WsForge.instance.getClass().getClassLoader(), new Class[]{CommandSender.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("isOp")) {
                            return true;
                        }
                        return method.invoke(player, args);
                    });
            List list = WsForge.forge.getConfig().getList("DrawName." + itemStack.getItemMeta().getDisplayName() + ".commands");
            for (Iterator it2 = list.iterator(); it2.hasNext(); ) {
                String[] s = it2.next().toString().split("#");
                switch (s[0]) {
                    case "op":
                        getServer().dispatchCommand(op, s[1].replaceAll("%player%", player.getName()).replaceAll("&", "§"));
                        break;
                    case "player":
                        getServer().dispatchCommand(player, s[1].replaceAll("%player%", player.getName()).replaceAll("&", "§"));
                        break;
                    case "console":
                        getServer().dispatchCommand(Bukkit.getConsoleSender(), s[1].replaceAll("%player%", player.getName()).replaceAll("&", "§"));
                        break;
                }
            }
            //发送全服消息
            if (WsForge.forge.getConfig().getBoolean("DrawName." + itemStack.getItemMeta().getDisplayName() + ".broadcast")) {
                Message.sendAllPlayerMessage(WsForge.forge.getConfig().getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".broadcastMessage").replace("%item%", drawItem.getItemMeta().getDisplayName().toString()));
            }
            //给予经验
            Random random = new Random();
            String[] s = WsForge.forge.getConfig().getString("DrawName." + itemStack.getItemMeta().getDisplayName() + ".giveExp").split("-");
            int max = Integer.parseInt(s[1]);
            int min = Integer.parseInt(s[0]);
            int exp = random.nextInt(max - min + 1) + min;
            Map.exp.put(player.getUniqueId(), Map.exp.get(player.getUniqueId()) + exp);
            //删除图纸
            player.getOpenInventory().setItem(13, null);
            //锻造信息
            Message.sendMessage(player, WsForge.instance.getConfig().getString("Message.success").replace("&", drawItem.getItemMeta().getDisplayName().toString()));
            Message.sendAllPlayerMessage("牛逼克拉斯");
        } else {
            Message.sendMessage(player, "&c&l你的金钱不足,无法锻造");
            player.closeInventory();
        }
    }
}
