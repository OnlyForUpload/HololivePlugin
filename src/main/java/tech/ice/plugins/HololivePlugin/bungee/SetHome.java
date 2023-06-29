package tech.ice.plugins.HololivePlugin.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SetHome extends Command {
    public SetHome() {
        super("seth","","sethome");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            TextComponent text = new TextComponent("用法：/seth <name>");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        File file = new File(Main.HololivePlugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
        Configuration user;

        try {
            user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (user.getSection("homes").getKeys().size() >= 2) {
            TextComponent text = new TextComponent("每個人最多只能設置 2 個家點");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        UUID uuid = UUID.randomUUID();
        Main.sethomes.put(uuid, player);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("location");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(uuid.toString());
        out.writeUTF(args[0]);

        player.getServer().sendData("hololive:plugin",out.toByteArray());
    }

    public static void save(ProxiedPlayer player, String name, UUID world, double x, double y, double z, float yaw, float pitch) {
        UUID uuid = player.getUniqueId();
        File file = new File(Main.HololivePlugin.getDataFolder() + "/userdata/" + uuid + ".yml");
        Configuration user;

        try {
            user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Configuration home = user.getSection("homes." + name);
        home.set("world", world.toString());
        home.set("x", x);
        home.set("y", y);
        home.set("z", z);
        home.set("yaw", yaw);
        home.set("pitch", pitch);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(user, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BaseComponent[] text = new ComponentBuilder("成功設置目前位置為家點 ").color(ChatColor.GOLD)
                .append(name).color(ChatColor.AQUA).create();
        player.sendMessage(text);
    }
}
