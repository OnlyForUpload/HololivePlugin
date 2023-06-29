package tech.ice.plugins.HololivePlugin.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MaxCheck implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        String uuid = ((Player) commandSender).getPlayer().getUniqueId().toString();

        File user = new File(Main.HololivePlugin.getDataFolder() + "/users.yml");
        YamlConfiguration users = YamlConfiguration.loadConfiguration(user);

        if (users.get(uuid) == null) {
            commandSender.sendMessage("§b目前你可向官方商店出售的價格上限為 0.0/" + Main.Max);
            return true;
        }
        commandSender.sendMessage("§b目前你可向官方商店出售的價格上限為 " + users.getDouble(uuid) + "/" + Main.Max);
        return true;
    }
}
