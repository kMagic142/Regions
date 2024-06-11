package ro.kmagic.regions.objects;

import ro.kmagic.regions.enums.FlagMode;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Region {

    private String name;
    private List<UUID> members;
    private HashMap<String, FlagMode> flags;
    private Cuboid cuboid;

    public Region(String name, List<UUID> members, HashMap<String, FlagMode> flags, Cuboid cuboid) {
        this.name = name;
        this.members = members;
        this.flags = flags;
        this.cuboid = cuboid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMembers(UUID uuid) {
        this.members.add(uuid);
    }

    public void removeMembers(UUID uuid) {
        this.members.remove(uuid);
    }

    public boolean containsMembers(UUID uuid) {
        return this.members.contains(uuid);
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public HashMap<String, FlagMode> getFlags() {
        return flags;
    }

    public void addFlag(String flag, FlagMode value) {
        this.flags.put(flag, value);
    }

    public void removeFlag(String flag) {
        this.flags.remove(flag);
    }

    public boolean containsFlag(String flag) {
        return this.flags.containsKey(flag);
    }

    public void setFlags(HashMap<String, FlagMode> flags) {
        this.flags = flags;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }
}
