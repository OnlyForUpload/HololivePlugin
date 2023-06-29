package tech.ice.plugins.HololivePlugin.paper;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.api.QuickShopAPI;

public class Main extends JavaPlugin {
    public static Main HololivePlugin;
    public static double Max;
    public static QuickShopAPI quickShopAPI;

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "hololive:plugin", new Channel());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "hololive:plugin");
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginCommand("renew").setExecutor(new Renew());
        getServer().getPluginCommand("delete").setExecutor(new Delete());
        getServer().getPluginCommand("fixnpc").setExecutor(new FixNPC());
        getServer().getPluginCommand("switch").setExecutor(new Switch());
        getServer().getPluginCommand("maxcheck").setExecutor(new MaxCheck());
        HololivePlugin = this;
        Max = Config.load().getDouble("max");
        Plugin quickshopPlugin = getServer().getPluginManager().getPlugin("QuickShop");
        if (quickshopPlugin != null && quickshopPlugin.isEnabled()) {
            quickShopAPI = (QuickShopAPI) quickshopPlugin;
        }
    }

    public Plugin getPlugin() {
        return this;
    }
}
