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

import static tech.ice.plugins.HololivePlugin.bungee.Main.progress;

public class Tpy extends Command {
    public Tpy() {
        super("tpyes","","tpaccept");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (progress.containsKey((ProxiedPlayer) sender)) {
            ProxiedPlayer p1 = progress.get((ProxiedPlayer) sender).getKey();
            ProxiedPlayer p2 = (ProxiedPlayer) sender;

            BaseComponent[] agree1 = new ComponentBuilder(p2.getName()).color(ChatColor.AQUA)
                    .append(" 接受了你的傳送請求").color(ChatColor.GOLD).create();

            BaseComponent[] agree2 = new ComponentBuilder("你接受了 ").color(ChatColor.GOLD)
                    .append(p1.getName()).color(ChatColor.AQUA)
                    .append(" 的傳送請求").color(ChatColor.GOLD).create();

            p1.sendMessage(agree1);
            p2.sendMessage(agree2);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(progress.get((ProxiedPlayer) sender).getValue());
            out.writeUTF(p1.getUniqueId().toString());
            out.writeUTF(p2.getUniqueId().toString());

            p1.getServer().sendData("hololive:plugin", out.toByteArray());

            progress.remove((ProxiedPlayer) sender);
            return;
        }

        TextComponent text = new TextComponent("你沒有任何待處理的傳送請求");
        text.setColor(ChatColor.RED);
        sender.sendMessage(text);
    }
}
