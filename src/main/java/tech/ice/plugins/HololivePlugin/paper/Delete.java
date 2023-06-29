package tech.ice.plugins.HololivePlugin.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

public class Delete implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof org.bukkit.entity.Player)
            return false;

        try {
            File user = new File(Main.HololivePlugin.getDataFolder() + "/users.yml");
            user.delete();
            user.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
