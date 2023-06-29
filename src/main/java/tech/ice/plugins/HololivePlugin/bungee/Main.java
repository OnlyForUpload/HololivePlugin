package tech.ice.plugins.HololivePlugin.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends Plugin {
    public static Main HololivePlugin;
    public static HashMap<UUID, ProxiedPlayer> sethomes = new HashMap<>();
    public static HashMap<ProxiedPlayer, Map.Entry<ProxiedPlayer, String>> progress = new HashMap<>();

    @Override
    public void onEnable() {
        getProxy().registerChannel("hololive:plugin");
        getProxy().getPluginManager().registerCommand(this, new Tpa());
        getProxy().getPluginManager().registerCommand(this, new Tph());
        getProxy().getPluginManager().registerCommand(this, new Tpy());
        getProxy().getPluginManager().registerCommand(this, new Tpn());
        getProxy().getPluginManager().registerCommand(this, new Home());
        getProxy().getPluginManager().registerCommand(this, new SetHome());
        getProxy().getPluginManager().registerCommand(this, new DelHome());
        getProxy().getPluginManager().registerListener(this, new Listeners());
        HololivePlugin = this;

        getProxy().getPluginManager().registerCommand(this, new Migrate());

    }
}
