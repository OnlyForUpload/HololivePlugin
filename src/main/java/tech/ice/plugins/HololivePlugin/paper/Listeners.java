package tech.ice.plugins.HololivePlugin.paper;

import com.github.puregero.multilib.DataStorageImpl;
import com.github.puregero.multilib.MultiLib;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.maxgamer.quickshop.api.event.ShopPurchaseEvent;

import su.nightexpress.excellentcrates.api.event.CrateObtainRewardEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerPurchase(ShopPurchaseEvent event) throws IOException {
        if (MultiLib.isExternalPlayer(Main.HololivePlugin.getServer().getPlayer(event.getPurchaser())) || event.isCancelled() || !Utils.getShops().contains(event.getShop())) return;
        String uuid = event.getPurchaser().toString();
        String name = event.getShop().getItem().getType().toString();

        File folder = Main.HololivePlugin.getDataFolder();
        if (!folder.exists()) folder.mkdir();
        File user = new File(folder + "/users.yml");
        File item = new File(folder + "/items.yml");
        if (!user.exists()) user.createNewFile();
        if (!item.exists()) item.createNewFile();
        YamlConfiguration users = YamlConfiguration.loadConfiguration(user);
        YamlConfiguration items = YamlConfiguration.loadConfiguration(item);

        if (users.get(uuid) == null) {
            users.set(uuid, event.getTotal());
            items.set(name + ".Amount", items.getInt(name + ".Amount") + event.getAmount());
            items.set(name + ".Prices", items.getDouble(name + ".Prices") + event.getTotal());
            users.save(user);
            items.save(item);
            return;
        }

        if (users.getDouble(uuid) + event.getTotal() <= Main.Max) {
            users.set(uuid, users.getDouble(uuid) + event.getTotal());
            items.set(name + ".Amount", items.getInt(name + ".Amount") + event.getAmount());
            items.set(name + ".Prices", items.getDouble(name + ".Prices") + event.getTotal());
            users.save(user);
            items.save(item);
            return;
        }

        event.setCancelled(true);
        Main.HololivePlugin.getServer().getPlayer(UUID.fromString(uuid)).sendMessage("§c你嘗試售出的價格（" + event.getTotal() + "）已經超出今天的上限（" + users.getDouble(uuid) + "/" + Main.Max + "）");
    }

    @EventHandler
    public void onPlayerObtainReward(CrateObtainRewardEvent event) {
        if (event.getReward().isBroadcast())
            Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "discord bcast #1076478434571735230 抽獎系統 » 玩家 " + PlaceholderAPI.setPlaceholders(event.getPlayer(), "%essentials_nickname%") + " 剛剛從 " + event.getCrate().getName().replaceAll("§.", "") + " 獲得了 " + event.getReward().getName().replaceAll("§.", "") + " 獎勵!");
        else Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "discord bcast #1091757876550307900 抽獎系統 » 玩家 " + PlaceholderAPI.setPlaceholders(event.getPlayer(), "%essentials_nickname%") + " 剛剛從 " + event.getCrate().getName().replaceAll("§.", "") + " 獲得了 " + event.getReward().getName().replaceAll("§.", "") + " 獎勵!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            Utils.update(event.getPlayer(), event.getItem(), event.getPlayer().getInventory());
            return;
        }

        if (event.getClickedBlock() == null || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock().getType().isBlock())
            if (event.getClickedBlock().getType().equals(Material.CARTOGRAPHY_TABLE)) event.setCancelled(true);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType().equals(ServerLoadEvent.LoadType.STARTUP)) new Utils().check();
        DataStorageImpl data = MultiLib.getDataStorage();
        data.set("skip", 0);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) throws ExecutionException, InterruptedException {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }
        DataStorageImpl data = MultiLib.getDataStorage();
        data.set("skip", data.getInt("skip").get() + 1);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) throws ExecutionException, InterruptedException {
        DataStorageImpl data = MultiLib.getDataStorage();
        if (data.getInt("skip").get() == 0) return;
        data.set("skip", data.getInt("skip").get() - 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getTitle().equals("界伏盒預覽")) {
            if (!event.getWhoClicked().getInventory().contains(Utils.thread.get(event.getWhoClicked().getUniqueId()))) event.getWhoClicked().closeInventory();
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getType().toString().contains("SHULKER_BOX")) event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            Utils.update(player, event.getCurrentItem(), event.getClickedInventory());
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getType().equals(InventoryType.SHULKER_BOX) && event.getDestination().getType().equals(InventoryType.SHULKER_BOX)) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (event.getView().getTitle().equals("界伏盒預覽")) {
            ItemStack shulkerBox = Utils.thread.get(player.getUniqueId());
            if (!event.getPlayer().getInventory().contains(Utils.thread.get(event.getPlayer().getUniqueId()))) return;
            BlockStateMeta im = (BlockStateMeta) shulkerBox.getItemMeta();
            assert im != null;
            ShulkerBox shulker = (ShulkerBox) im.getBlockState();
            shulker.getInventory().setContents(event.getInventory().getContents());
            im.setBlockState(shulker);
            shulkerBox.setItemMeta(im);

            player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_CLOSE, 1, 1);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getInventory().contains(Material.FILLED_MAP)) event.setCancelled(true);
    }
}
