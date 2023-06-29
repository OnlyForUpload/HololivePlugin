package tech.ice.plugins.HololivePlugin.paper;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static YamlConfiguration load() {
        try {
            File file = new File(Main.HololivePlugin.getDataFolder() + "/config.yml");
            if (!file.exists()) file.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.get("max") == null) config.set("max", 300000.0);
            config.save(file);
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
