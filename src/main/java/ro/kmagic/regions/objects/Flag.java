package ro.kmagic.regions.objects;

import ro.kmagic.regions.enums.FlagMode;

import java.util.List;

public class Flag {

    private final String id;
    private String name;
    private List<String> description;
    private FlagMode defaultValue;

    public Flag (String id, String name, List<String> description, FlagMode defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public FlagMode getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(FlagMode defaultValue) {
        this.defaultValue = defaultValue;
    }
}
