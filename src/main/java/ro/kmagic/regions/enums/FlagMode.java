package ro.kmagic.regions.enums;

public enum FlagMode {
    EVERYONE("Everyone"),
    WHITELIST("Whitelist"),
    NONE("None");

    private final String name;

    FlagMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
