package tech.ice.plugins.HololivePlugin.paper;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class Switch implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Inventory inv = ((Player) commandSender).getInventory();
        ArrayList<ItemStack> list = new ArrayList<>();
        int i = 0;
        for (ItemStack stack : inv) {
            if (stack != null) {
                if (stack.getType().equals(Material.ITEM_FRAME)) {
                    if (!Objects.requireNonNull(stack.getItemMeta()).getAsString().contains("Invisible:1b")) {
                        i = i + stack.getAmount();
                        list.add(stack);
                    }
                }
            }
        }

        if (i == 0) {
            commandSender.sendMessage("§c你的背包沒有任何的物品展示框需要兌換成隱形展示框");
            return true;
        }

        if (Double.parseDouble(PlaceholderAPI.setPlaceholders(((Player) commandSender).getPlayer(), "%xconomy_balance_value%")) >= i * 1500) {
            for (ItemStack itemStack : list) {
                inv.remove(itemStack);
            }
            Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "minecraft:give " + commandSender.getName() + " item_frame{EntityTag:{Invisible:1b}} " + i);
            Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "bal take " + commandSender.getName() + " " + i * 1500);
            commandSender.sendMessage("§b你的背包有 " + i + " 個物品展示框已兌換成隱形展示框，共花費 " + i * 1500 + " 個伺服器貨幣");
        } else {
            commandSender.sendMessage("§c你所擁有的金錢不足以兌換身上所有的展示框成隱形展示框");
        }

        return true;
    }
}
