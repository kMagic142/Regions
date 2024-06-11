package ro.kmagic.regions.storage;

import ro.kmagic.regions.enums.FlagMode;
import ro.kmagic.regions.objects.Flag;

import java.util.HashMap;
import java.util.List;

public class FlagStorage {

    private final HashMap<String, Flag> flagsMap;

    public FlagStorage() {
        this.flagsMap = new HashMap<>();
        flagsMap.put("block-break", new Flag(
                "block-break",
                "&cBlock Break",
                List.of("&7Control the player's ability to break blocks"),
                FlagMode.WHITELIST)
        );
        flagsMap.put("block-place", new Flag(
                "block-place",
                "&cBlock Place",
                List.of("&7Control the player's ability to place blocks"),
                FlagMode.WHITELIST)
        );
        flagsMap.put("interact", new Flag(
                "interact",
                "&cInteract",
                List.of("&7Control the player's ability to interact with blocks"),
                FlagMode.WHITELIST)
        );
        flagsMap.put("entity-damage", new Flag(
                "entity-damage",
                "&cEntity Damage",
                List.of("&7Control the player's ability to damage entities"),
                FlagMode.WHITELIST)
        );
    }

    public void addFlag(String id, Flag flag) {
        this.flagsMap.put(id, flag);
    }

    public void removeFlag(String id) {
        this.flagsMap.remove(id);
    }

    public Flag getFlag(String id) {
        return this.flagsMap.get(id);
    }

    public HashMap<String, Flag> getFlagsMap() {
        return flagsMap;
    }
}
