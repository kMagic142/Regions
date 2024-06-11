package ro.kmagic.regions.configuration;

import ro.kmagic.regions.Regions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private final Regions main;

    public ConfigManager(Regions main) {
        this.main = main;
        createConfig("config");
    }

    public void createConfig(String file) {
        if (!(new File(main.getDataFolder(), file + ".yml")).exists()) {
            main.saveResource(file + ".yml", false);
        }
    }

    public FileConfiguration getConfig(String file) {
        File archive = new File(main.getDataFolder() + File.separator + file + ".yml");
        return YamlConfiguration.loadConfiguration(archive);
    }

}
