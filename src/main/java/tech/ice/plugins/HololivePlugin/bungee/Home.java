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
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class Home extends Command implements TabExecutor {
    public Home() {
        super("home","","homes");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            TextComponent text = new TextComponent("用法：/home <name>");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID uuid = player.getUniqueId();

        File file = new File(Main.HololivePlugin.getDataFolder() + "/userdata/" + uuid + ".yml");
        Configuration user;

        try {
            user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Configuration home = user.getSection("homes." + args[0]);

        if (home.getKeys().size() != 6) {
            BaseComponent[] text = new ComponentBuilder("找不到家點 ").color(ChatColor.RED)
                    .append(args[0]).color(ChatColor.AQUA).create();
            sender.sendMessage(text);
            return;
        }

        Duration timeLeft = Cooldown.getRemainingCooldown(((ProxiedPlayer) sender).getUniqueId());

        if (!timeLeft.isZero() && !timeLeft.isNegative()) {
            BaseComponent[] text = new ComponentBuilder("你可以在 ").color(ChatColor.RED)
                    .append(String.valueOf(timeLeft.toSeconds())).color(ChatColor.AQUA)
                    .append(" 秒後再次使用此指令").color(ChatColor.RED).create();
            sender.sendMessage(text);
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(home.getString("world"));
        out.writeDouble(home.getDouble("x"));
        out.writeDouble(home.getDouble("y"));
        out.writeDouble(home.getDouble("z"));
        out.writeFloat(home.getFloat("yaw"));
        out.writeFloat(home.getFloat("pitch"));

        ((ProxiedPlayer) sender).getServer().sendData("hololive:plugin", out.toByteArray());

        BaseComponent[] text = new ComponentBuilder("正在傳送到 ").color(ChatColor.GOLD)
                .append(args[0]).color(ChatColor.AQUA).create();
        sender.sendMessage(text);
        Cooldown.setCooldown(player.getUniqueId());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> homes = new ArrayList<>();
        if (args.length == 1) {
            try {
                File file = new File(Main.HololivePlugin.getDataFolder() + "/userdata/" + ((ProxiedPlayer) sender).getUniqueId() + ".yml");
                Configuration user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                homes.addAll(user.getSection("homes").getKeys());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return homes;
    }
}
