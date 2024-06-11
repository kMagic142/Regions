package ro.kmagic.regions.managers;

import ro.kmagic.regions.enums.FlagMode;
import ro.kmagic.regions.objects.BlockPosition;
import ro.kmagic.regions.objects.Flag;
import ro.kmagic.regions.objects.Region;
import ro.kmagic.regions.storage.FlagStorage;
import ro.kmagic.regions.storage.RegionStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RegionManager {

    private final FlagStorage flagStorage;
    private final RegionStorage regionStorage;

    public RegionManager(FlagStorage flagStorage, RegionStorage regionStorage) {
        this.flagStorage = flagStorage;
        this.regionStorage = regionStorage;
    }

    public List<Region> getRegionsAt(String world, BlockPosition blockPosition) {
        return CompletableFuture.supplyAsync(() -> {
            List<Region> regions = new ArrayList<>();
            for (Region region : regionStorage.getRegionsMap().values()) {
                if (region.getCuboid().contains(world, blockPosition)) {
                    regions.add(region);
                }
            }
            return regions;
        }).join();
    }

    public FlagMode getFlagValue(Region region, String flagId) {
        if(region.getFlags().containsKey(flagId)) return region.getFlags().get(flagId);

        Flag flag = flagStorage.getFlag(flagId);
        if(flag == null) return null;
        return flag.getDefaultValue();
    }
}
