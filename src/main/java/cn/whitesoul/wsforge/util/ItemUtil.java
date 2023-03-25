package cn.whitesoul.wsforge.util;

import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    // 获取背包指定物品的数量
    public static int getAmount(ItemStack[] items, ItemStack item) {
        int amount = 0;
        for (ItemStack i : items) {
            if (i != null && i.isSimilar(item)) {
                amount += i.getAmount();
            }
        }
        return amount;
    }
    // 移除背包物品数量
    public static void removeAmount(ItemStack[] items, ItemStack item, int amount) {
        for (ItemStack i : items) {
            if (i != null && i.isSimilar(item)) {
                if (i.getAmount() > amount) {
                    i.setAmount(i.getAmount() - amount);
                    break;
                } else {
                    amount -= i.getAmount();
                    i.setAmount(0);
                }
            }
        }
    }

}
