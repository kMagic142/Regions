package ro.kmagic.regions.storage;

import ro.kmagic.regions.database.RegionsDB;
import ro.kmagic.regions.objects.Region;

import java.util.HashMap;

public class RegionStorage {

    private final RegionsDB regionsDB;
    private final HashMap<String, Region> regionsMap;

    public RegionStorage(RegionsDB regionsDB) {
        this.regionsDB = regionsDB;
        this.regionsMap = regionsDB.loadAllRegions();
    }

    public void addRegion(String id, Region region) {
        this.regionsMap.put(id, region);
    }

    public void removeRegion(String id) {
        this.regionsMap.remove(id);
    }

    public Region getRegion(String id) {
        return this.regionsMap.get(id);
    }

    public HashMap<String, Region> getRegionsMap() {
        return regionsMap;
    }

    public boolean isRegionExists(String id) {
        return this.regionsMap.containsKey(id);
    }

    public void createRegion(String id, Region region) {
        this.regionsMap.put(id, region);
        this.regionsDB.updateRegion(region);
    }

    public void deleteRegion(String id){
        this.regionsMap.remove(id);
        regionsDB.removeRegion(id);
    }

    public void updateRegion(Region region){
        this.regionsMap.put(region.getName(), region);
        regionsDB.updateRegion(region);
    }
}
