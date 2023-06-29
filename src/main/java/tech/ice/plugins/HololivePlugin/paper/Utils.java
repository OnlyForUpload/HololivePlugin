package tech.ice.plugins.HololivePlugin.paper;

import com.github.puregero.multilib.DataStorageImpl;
import com.github.puregero.multilib.MultiLib;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.maxgamer.quickshop.api.shop.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Utils {

    public static Map<UUID, ItemStack> thread = new HashMap<>();

    public void check() {
        DataStorageImpl data = MultiLib.getDataStorage();
        Main.HololivePlugin.getServer().getScheduler().runTaskTimerAsynchronously(Main.HololivePlugin.getPlugin(), () -> {
            try {
                if (MultiLib.getAllOnlinePlayers().size() == 0 || data.getInt("skip").get() == 0) return;
                for (Player player : MultiLib.getAllOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§e目前有 " + data.getInt("skip").get() + " 位玩家已睡眠"));
                }
                double percentage = (double) data.getInt("skip").get() / MultiLib.getAllOnlinePlayers().size() * 100;
                System.out.println(percentage);
                if (percentage >= 25) {
                    Main.HololivePlugin.getServer().getScheduler().runTask(Main.HololivePlugin.getPlugin(), () -> Main.HololivePlugin.getServer().getWorlds().forEach(world -> world.setTime(1000L)));
                    for (Player player : MultiLib.getAllOnlinePlayers()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§e達成進度, 伺服器已跳過夜晚"));
                    }
                    data.set("skip", 0);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }, 0L, 20L);
    }

    public static void update(Player player, ItemStack item, Inventory inv) {
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!item.getType().toString().contains("SHULKER_BOX")) return;
        if (inv == null) return;
        if (!inv.getType().equals(InventoryType.PLAYER)) return;

        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
        if (!(blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox)) return;

        Inventory shulkerPreview = Main.HololivePlugin.getServer().createInventory(null, InventoryType.SHULKER_BOX, "界伏盒預覽");
        shulkerPreview.setContents(shulkerBox.getInventory().getContents());

        blockStateMeta.setBlockState(shulkerBox);
        item.setItemMeta(blockStateMeta);

        player.openInventory(shulkerPreview);
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
        thread.put(player.getUniqueId(), item);
    }

    public static ArrayList<Shop> getShops() {
        ArrayList<Shop> shops = new ArrayList<>();
        for (int i = -139; i >= -152; i--) {
            System.out.println(i);
            shops.add(Main.quickShopAPI.getShopManager().getShop(new Location(Main.HololivePlugin.getServer().getWorld("world"), 247, 71, i)));
            shops.add(Main.quickShopAPI.getShopManager().getShop(new Location(Main.HololivePlugin.getServer().getWorld("world"), 247, 69, i)));
            shops.add(Main.quickShopAPI.getShopManager().getShop(new Location(Main.HololivePlugin.getServer().getWorld("world"), 255, 71, i)));
            shops.add(Main.quickShopAPI.getShopManager().getShop(new Location(Main.HololivePlugin.getServer().getWorld("world"), 255, 69, i)));
        }
        return shops;
    }
}
