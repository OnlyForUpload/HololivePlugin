package tech.ice.plugins.HololivePlugin.bungee;

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
import java.util.ArrayList;
import java.util.UUID;

public class DelHome extends Command implements TabExecutor {
    public DelHome() {
        super("delh","","delhome");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            TextComponent text = new TextComponent("用法：/delh <name>");
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

        user.set("homes." + args[0], null);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(user, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
