package ro.kmagic.regions.database;

import ro.kmagic.regions.enums.FlagMode;
import ro.kmagic.regions.objects.Cuboid;
import ro.kmagic.regions.objects.Region;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegionsDB {

    private final String TABLE = "regions";

    public RegionsDB() {
        createTable();
    }

    public void createTable() {
        CompletableFuture.runAsync(() -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    "name VARCHAR(64) NOT NULL PRIMARY KEY," +
                    "members VARCHAR(10000) NOT NULL," +
                    "flags VARCHAR(300) NOT NULL," +
                    "cuboid VARCHAR(100) NOT NULL" +
                    ");";
            try (PreparedStatement statement = Database.connection.prepareStatement(sql)) {
                statement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public HashMap<String, Region> loadAllRegions() {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM `" + TABLE + "`";
            try (PreparedStatement statement = Database.connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                HashMap<String, Region> regions = new HashMap<>();
                while (resultSet.next()) {
                    List<UUID> members = new ArrayList<>();
                    if(!resultSet.getString(2).isEmpty()) {
                        String[] membersString = resultSet.getString(2).split(",");
                        for (String member : membersString) {
                            members.add(UUID.fromString(member));
                        }
                    }

                    HashMap<String, FlagMode> flags = new HashMap<>();
                    if(!resultSet.getString(3).isEmpty()) {
                        String[] flagsString = resultSet.getString(3).split(",");
                        for (String flag : flagsString) {
                            String[] flagSplit = flag.split(":");
                            flags.put(flagSplit[0], FlagMode.valueOf(flagSplit[1]));
                        }
                    }

                    regions.put(
                            resultSet.getString(1),
                            new Region(
                                    resultSet.getString(1),
                                    members,
                                    flags,
                                    new Cuboid(resultSet.getString(4))
                            )
                    );
                }
                return regions;
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        }).join();
    }

    public void updateRegion(Region region) {
        CompletableFuture.runAsync(() -> {
            String sql = "REPLACE INTO " + TABLE + " VALUES(?,?,?,?)";
            try {
                PreparedStatement statement = Database.connection.prepareStatement(sql);
                statement.setString(1, region.getName());
                statement.setString(2, StringUtils.join(region.getMembers(), ","));
                statement.setString(3, flagsToString(region.getFlags()));
                statement.setString(4, region.getCuboid().serialize());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void removeRegion(String name) {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM " + TABLE + " WHERE name = ?";
            try {
                PreparedStatement statement = Database.connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String flagsToString(HashMap<String, FlagMode> flags) {
        StringBuilder builder = new StringBuilder();

        for (String flag : flags.keySet()) {
            builder.append(flag).append(":").append(flags.get(flag)).append(",");
        }

        return builder.toString().toString();
    }
}