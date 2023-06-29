package tech.ice.plugins.HololivePlugin.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Migrate extends Command {
    public Migrate() {
        super("migrate","","");
    }

    public void execute(CommandSender sender, String[] args) {
        File folder = new File(Main.HololivePlugin.getDataFolder() + "/ess/");
        for (File file : folder.listFiles()) {
            try {
                Configuration user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                Configuration homes = user.getSection("homes");
                if (homes.getKeys().size() == 0) {
                    System.out.println(0);
                    continue;
                }
                File done = new File(Main.HololivePlugin.getDataFolder() + "/userdata/" + file.toString().replace("plugins/HololivePlugin/ess/", ""));
                if (!done.exists()) done.createNewFile();
                Configuration migrate = ConfigurationProvider.getProvider(YamlConfiguration.class).load(done);
                for (String name : homes.getKeys()) {
                    Configuration home = user.getSection("homes." + name);
                    migrate.set("homes." + name + ".world", home.getString("world"));
                    migrate.set("homes." + name + ".x", home.getDouble("x"));
                    migrate.set("homes." + name + ".y", home.getDouble("y"));
                    migrate.set("homes." + name + ".z", home.getDouble("z"));
                    migrate.set("homes." + name + ".yaw", home.getFloat("yaw"));
                    migrate.set("homes." + name + ".pitch", home.getFloat("pitch"));
                }
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(migrate, done);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
