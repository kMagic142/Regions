package ro.kmagic.regions;

import fr.minuskube.inv.InventoryManager;
import ro.kmagic.regions.api.RegionAPI;
import ro.kmagic.regions.commands.RegionCommand;
import ro.kmagic.regions.configuration.ConfigManager;
import ro.kmagic.regions.database.Database;
import ro.kmagic.regions.database.RegionsDB;
import ro.kmagic.regions.listeners.InternalListener;
import ro.kmagic.regions.listeners.PlayerListener;
import ro.kmagic.regions.managers.RegionManager;
import ro.kmagic.regions.storage.FlagStorage;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.storage.UserStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

public final class Regions extends JavaPlugin {

    private static Regions instance;
    private ConfigManager configManager;
    private static RegionAPI regionAPI;
    private Logger logger;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        logger = getLogger();
        instance = this;

        inventoryManager = new InventoryManager(Regions.getInstance());
        inventoryManager.init();

        configManager = new ConfigManager(this);
        Database.connect(logger, configManager.getConfig("config"));
        RegionsDB regionsDB = new RegionsDB();

        FlagStorage flagStorage = new FlagStorage();
        RegionStorage regionStorage = new RegionStorage(regionsDB);
        UserStorage userStorage = new UserStorage();

        RegionManager regionManager = new RegionManager(flagStorage, regionStorage);
        regionAPI = new RegionAPI(flagStorage, regionStorage, userStorage, regionManager);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(userStorage, regionStorage, regionManager), this);
        this.getServer().getPluginManager().registerEvents(new InternalListener(flagStorage, logger), this);
        this.getCommand("region").setExecutor(new RegionCommand(userStorage, regionStorage, flagStorage));

        scheduleTask();
    }

    @Override
    public void onDisable() {
        Database.disconnect(logger);
    }

    public static RegionAPI getRegionAPI() {
        return regionAPI;
    }

    private void scheduleTask() {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleAsyncRepeatingTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                Database.reloadConnection(logger, configManager.getConfig("config"));
            }
        }, 20L * 60 * 60, 20L * 60 * 60);
    }

    public static Regions getInstance() {
        return instance;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
