package ro.kmagic.regions.objects;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {

    private final String world;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Cuboid(String serializedCuboid) {
        String[] parts = serializedCuboid.split(",");
        this.world = parts[0];
        this.x1 = Math.min(Integer.parseInt(parts[1]), Integer.parseInt(parts[4]));
        this.y1 = Math.min(Integer.parseInt(parts[2]), Integer.parseInt(parts[5]));
        this.z1 = Math.min(Integer.parseInt(parts[3]), Integer.parseInt(parts[6]));

        this.x2 = Math.max(Integer.parseInt(parts[1]), Integer.parseInt(parts[4]));
        this.y2 = Math.max(Integer.parseInt(parts[2]), Integer.parseInt(parts[5]));
        this.z2 = Math.max(Integer.parseInt(parts[3]), Integer.parseInt(parts[6]));
    }

    public Cuboid(String world, BlockPosition location, BlockPosition location2) {
        this.world = world;
        this.x1 = Math.min(location.getX(), location2.getX());
        this.y1 = Math.min(location.getY(), location2.getY());
        this.z1 = Math.min(location.getZ(), location2.getZ());

        this.x2 = Math.max(location.getX(), location2.getX());
        this.y2 = Math.max(location.getY(), location2.getY());
        this.z2 = Math.max(location.getZ(), location2.getZ());
    }

    public boolean worldEquals(String world) {
        return this.world.equalsIgnoreCase(world);
    }

    public boolean contains(BlockPosition location) {
        return location.getX() >= x1 && location.getX() <= x2 && location.getY() >= y1 && location.getY() <= y2 && location.getZ() >= z1 && location.getZ() <= z2;
    }

    public boolean contains(String world, BlockPosition location) {
        if(!this.world.equalsIgnoreCase(world)) return false;
        return location.getX() >= x1 && location.getX() <= x2 && location.getY() >= y1 && location.getY() <= y2 && location.getZ() >= z1 && location.getZ() <= z2;
    }

    public String serialize(){
        return world + "," + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2;
    }

    public String getLocationString(){
        return "X: " + x1 + " Y: " + y1 + " Z: " + z1;
    }

    public List<BlockPosition> getBlocks() {
        List<BlockPosition> blockList = new ArrayList<>();
        for (int x = this.x1; x <= this.x2; x++) {
            for (int y = this.y1; y <= this.y2; y++) {
                for (int z = this.z1; z <= this.z2; z++) {
                    blockList.add(new BlockPosition(x, y, z));
                }
            }
        }
        return blockList;
    }

}
