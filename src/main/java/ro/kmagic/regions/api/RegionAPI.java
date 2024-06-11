package ro.kmagic.regions.api;

import ro.kmagic.regions.events.LoadFlagsEvent;
import ro.kmagic.regions.managers.RegionManager;
import ro.kmagic.regions.objects.Flag;
import ro.kmagic.regions.storage.FlagStorage;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.storage.UserStorage;
import org.bukkit.Bukkit;

public class RegionAPI {

    private final FlagStorage flagStorage;
    private final RegionStorage regionStorage;
    private final UserStorage userStorage;
    private final RegionManager regionManager;

    public RegionAPI(FlagStorage flagStorage, RegionStorage regionStorage, UserStorage userStorage, RegionManager regionManager) {
        this.flagStorage = flagStorage;
        this.regionStorage = regionStorage;
        this.userStorage = userStorage;
        this.regionManager = regionManager;
    }

    public FlagStorage getFlagStorage() {
        return flagStorage;
    }

    public RegionStorage getRegionStorage() {
        return regionStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public void loadCustomFlag(String flagId, Flag flag){
        Bukkit.getPluginManager().callEvent(new LoadFlagsEvent(flagId, flag));
    }
}
