package tech.ice.plugins.HololivePlugin.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FixNPC implements CommandExecutor {
    private boolean rate = false;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (rate) {
            commandSender.sendMessage("§c嘗試執行的速度太快，請稍候再試一次");
            return true;
        }
        Main.HololivePlugin.getServer().dispatchCommand(Main.HololivePlugin.getServer().getConsoleSender(), "citizens reload");
        Main.Max = Config.load().getDouble("max");
        rate = true;
        Main.HololivePlugin.getServer().getScheduler().runTaskLaterAsynchronously(Main.HololivePlugin, () -> {
            rate = false;
        }, 200L);
        return true;
    }
}
