package cn.whitesoul.wsforge.listener;

import cn.whitesoul.wsforge.util.ItemUtil;
import cn.whitesoul.wslib.item.WsItem;
import cn.whitesoul.wslib.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pers.neige.neigeitems.NeigeItems;
import pers.neige.neigeitems.item.ItemPack;
import pers.neige.neigeitems.manager.ItemManager;
import pers.neige.neigeitems.manager.ItemPackManager;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory().getTitle().equalsIgnoreCase("锻造台") && event.getClickedInventory() !=null ) {
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
                if (event.getClickedInventory() !=null && event.getClickedInventory().getItem(13) != null && event.getClickedInventory().getItem(13).getType() != Material.AIR) {
                    if (event.getClickedInventory().getItem(13).getAmount() == 1 ){
                    if (event.getClickedInventory().getItem(13).getItemMeta().getDisplayName().equalsIgnoreCase("图纸")) {
                        // 移除图纸
                        event.getClickedInventory().setItem(13,null);
                        // 给予锻造物品
                        ItemStack i = ItemManager.INSTANCE.getItemStack("ExampleItem",player);
                        event.getClickedInventory().setItem(4,i);
                        //发送消息
                        Message.sendMessage(player, "&a&l你锻造了一张纸");
                        Message.sendAllPlayerMessage("牛逼克拉斯");
                    }
                } else {
                        player.closeInventory();
                        Message.sendMessage(player, "你只能放一张图纸在这里！");
                    }
                } else {
                    player.closeInventory();
                    Message.sendMessage(player,"你没有放图纸在这里！");
                }
            }
        }
    }
        @EventHandler
        public void InventoryClose(InventoryCloseEvent event){
        if (event.getInventory().getTitle().equalsIgnoreCase("锻造台")){
            if (event.getInventory().getItem(13) !=null ){
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
            }
            if (event.getInventory().getItem(4) !=null ){
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(4));
            }
        }
    }
}
