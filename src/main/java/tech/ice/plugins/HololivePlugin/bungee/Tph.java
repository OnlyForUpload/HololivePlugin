package tech.ice.plugins.HololivePlugin.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static tech.ice.plugins.HololivePlugin.bungee.Main.progress;

public class Tph extends Command implements TabExecutor {
    public Tph() {
        super("tph","","tpahere");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            TextComponent text = new TextComponent("用法：/tph <player>");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        if (args[0].equals(sender.getName())) {
            TextComponent text = new TextComponent("你不能將傳送請求發送給自己");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
            BaseComponent[] text = new ComponentBuilder("找不到玩家 ").color(ChatColor.RED)
                    .append(args[0]).color(ChatColor.AQUA).create();
            sender.sendMessage(text);
            return;
        }

        if (progress.get(ProxyServer.getInstance().getPlayer(args[0])) != null) {
            if (progress.get(ProxyServer.getInstance().getPlayer(args[0])).equals(new AbstractMap.SimpleEntry<>((ProxiedPlayer) sender, "tph"))) {
                TextComponent text = new TextComponent("你已經向該玩家發送過了傳送請求");
                text.setColor(ChatColor.RED);
                sender.sendMessage(text);
                return;
            }
        }

        if (progress.containsKey(ProxyServer.getInstance().getPlayer(args[0]))) {
            TextComponent text = new TextComponent("該玩家還有其他的傳送請求未處理");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

        BaseComponent[] success = new ComponentBuilder("請求已傳送到 ").color(ChatColor.GOLD)
                .append(args[0]).color(ChatColor.AQUA)
                .append("\n該請求將在 120 秒內失效").color(ChatColor.GOLD).create();
        sender.sendMessage(success);

        BaseComponent[] receive = new ComponentBuilder(sender.getName()).color(ChatColor.AQUA)
                .append(" 請求你傳送到他那裡").color(ChatColor.GOLD).create();

        TextComponent desc = new TextComponent("若想接受傳送，執行 [/tpyes]\n若想拒絕傳送，執行 [/tpno]");
        desc.setColor(ChatColor.GOLD);

        TextComponent message = new TextComponent("或點我來同意這項傳送請求");
        message.setColor(ChatColor.LIGHT_PURPLE);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpyes"));

        player.sendMessage(receive);
        player.sendMessage(desc);
        player.sendMessage(message);

        progress.put(player, new AbstractMap.SimpleEntry<>((ProxiedPlayer) sender, "tpa"));

        ProxyServer.getInstance().getScheduler().schedule(Main.HololivePlugin, () -> {
            if (progress.containsKey(player)) {
                BaseComponent[] timeout = new ComponentBuilder("你發送給 ").color(ChatColor.GOLD)
                        .append(player.getName()).color(ChatColor.AQUA)
                        .append(" 的傳送請求失效了").color(ChatColor.GOLD).create();
                sender.sendMessage(timeout);
                progress.remove(player);
            }
        }, 120, TimeUnit.SECONDS);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> players = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            players.add(player.getName());
        }
        players.remove(sender.getName());
        return players;
    }
}
