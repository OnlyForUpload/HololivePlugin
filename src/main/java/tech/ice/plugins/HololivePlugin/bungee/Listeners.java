package tech.ice.plugins.HololivePlugin.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

import static tech.ice.plugins.HololivePlugin.bungee.Main.progress;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        progress.remove(player);
        for (Map.Entry<ProxiedPlayer, Map.Entry<ProxiedPlayer, String>> entry : progress.entrySet()) {
            if (entry.getValue().equals(new AbstractMap.SimpleEntry<>(player, "tpa")) || entry.getValue().equals(new AbstractMap.SimpleEntry<>(player, "tph"))) {
                progress.remove(entry.getKey());
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        File folder = Main.HololivePlugin.getDataFolder();
        if (!folder.exists()) folder.mkdir();
        File data = new File(folder + "/userdata/");
        if (!data.exists()) data.mkdir();
        File file = new File(folder + "/userdata/" + uuid + ".yml");

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equals("hololive:plugin")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            if (in.readUTF().equals("location")) {
                UUID uuid = UUID.fromString(in.readUTF());
                if (Main.sethomes.containsKey(uuid)) {
                    ProxiedPlayer player = Main.sethomes.get(uuid);
                    String name = in.readUTF();
                    UUID world = UUID.fromString(in.readUTF());
                    double x = in.readDouble();
                    double y = in.readDouble();
                    double z = in.readDouble();
                    float yaw = in.readFloat();
                    float pitch = in.readFloat();

                    SetHome.save(player, name, world, x, y, z, yaw, pitch);

                    Main.sethomes.remove(uuid);
                }
            }
        }
    }
}
