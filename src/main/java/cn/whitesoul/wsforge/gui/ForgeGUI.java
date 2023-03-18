package cn.whitesoul.wsforge.gui;

import cn.whitesoul.wslib.item.WsItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ForgeGUI {
    private static Inventory inventory;
    public static void create(){
        inventory = Bukkit.createInventory(null, 54, "锻造台");
        ItemStack button = new WsItem(Material.PAPER, 1, "§f锻造","§7点击锻造");
        inventory.setItem(22, button);
    }
    //打开锻造台
    public static void open(Player player){
        create();
        player.openInventory(inventory);
    }
}
