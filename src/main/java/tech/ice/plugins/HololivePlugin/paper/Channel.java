package tech.ice.plugins.HololivePlugin.paper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class Channel implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("hololive:plugin")) {

            ByteArrayDataInput in = ByteStreams.newDataInput(message);

            String type = in.readUTF();

            if (type.equals("teleport")) {
                Player p = Main.HololivePlugin.getServer().getPlayer(UUID.fromString(in.readUTF()));

                if (p == null) {
                    Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "discord bcast <@668431368488747008> null");
                    return;
                }

                UUID world = UUID.fromString(in.readUTF());
                double x = in.readDouble();
                double y = in.readDouble();
                double z = in.readDouble();
                float yaw = in.readFloat();
                float pitch = in.readFloat();

                Location home = new Location(Main.HololivePlugin.getPlugin().getServer().getWorld(world), x, y, z, yaw, pitch);
                p.teleport(home);

                return;
            }

            if (type.equals("location")) {
                Player p = Main.HololivePlugin.getServer().getPlayer(UUID.fromString(in.readUTF()));

                if (p == null) {
                    Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "discord bcast <@668431368488747008> null");
                    return;
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("location");
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeUTF(p.getWorld().getUID().toString());
                out.writeDouble(p.getLocation().getX());
                out.writeDouble(p.getLocation().getY());
                out.writeDouble(p.getLocation().getZ());
                out.writeFloat(p.getLocation().getYaw());
                out.writeFloat(p.getLocation().getPitch());

                p.getServer().sendPluginMessage(Main.HololivePlugin, "hololive:plugin", out.toByteArray());

                return;
            }

            Player p1 = Main.HololivePlugin.getServer().getPlayer(UUID.fromString(in.readUTF()));
            Player p2 = Main.HololivePlugin.getServer().getPlayer(UUID.fromString(in.readUTF()));

            if (p1 == null || p2 == null) {
                Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "discord bcast <@668431368488747008> null");
                return;
            }

            if (type.equals("tpa"))
                p1.teleport(p2);
            else if (type.equals("tph"))
                p2.teleport(p1);
        }
    }
}
