package ro.kmagic.regions.objects;

import ro.kmagic.regions.enums.EditMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EditUser {

    private final UUID uuid;
    private EditMode editMode;
    private String regionId;
    private BlockPosition firstPosition;
    private BlockPosition secondPosition;

    public EditUser(UUID uuid, EditMode editMode) {
        this.uuid = uuid;
        this.editMode = editMode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public EditMode getEditMode() {
        return editMode;
    }

    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public BlockPosition getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(BlockPosition firstPosition) {
        this.firstPosition = firstPosition;
    }

    public BlockPosition getSecondPosition() {
        return secondPosition;
    }

    public void setSecondPosition(BlockPosition secondPosition) {
        this.secondPosition = secondPosition;
    }
}
