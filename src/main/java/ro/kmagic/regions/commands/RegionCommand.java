package ro.kmagic.regions.commands;

import org.jetbrains.annotations.NotNull;
import ro.kmagic.regions.enums.FlagMode;
import ro.kmagic.regions.inventories.RegionInventory;
import ro.kmagic.regions.inventories.RegionListInventory;
import ro.kmagic.regions.objects.Cuboid;
import ro.kmagic.regions.objects.EditUser;
import ro.kmagic.regions.objects.Flag;
import ro.kmagic.regions.objects.Region;
import ro.kmagic.regions.storage.FlagStorage;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.storage.UserStorage;
import ro.kmagic.regions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.kmagic.regions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RegionCommand implements CommandExecutor {

    private final UserStorage userStorage;
    private final RegionStorage regionStorage;
    private final FlagStorage flagStorage;

    public RegionCommand(UserStorage userStorage, RegionStorage regionStorage, FlagStorage flagStorage) {
        this.userStorage = userStorage;
        this.regionStorage = regionStorage;
        this.flagStorage = flagStorage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.color("&cYou must be a player to execute this command!"));
                return false;
            }

            Player player = (Player) sender;
            if(!player.hasPermission("region.menu")) {
                player.sendMessage(Util.color("&cYou don't have permission to use this command."));
                return false;
            }

            EditUser user = userStorage.getUser(player.getUniqueId());
            RegionListInventory.getInventory(user, regionStorage).open(player);
            return false;
        } else {
            switch (args[0]) {
                case("create"): {
                    if(args.length != 2) {
                        sender.sendMessage(Util.color("&cUsage: /region create <name>"));
                        return false;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.color("&cYou must be a player to execute this command!"));
                        return false;
                    }

                    Player player = (Player) sender;
                    if(!player.hasPermission("region.create")) {
                        player.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    EditUser user = userStorage.getUser(player.getUniqueId());
                    String regionName = args[1];

                    if(regionStorage.isRegionExists(regionName)) {
                        player.sendMessage(Util.color("&cThis region already exists."));
                        return false;
                    }

                    if(user.getFirstPosition() == null || user.getSecondPosition() == null){
                        player.sendMessage(Util.color("&cYou must set the first and second position."));
                        return false;
                    }

                    Cuboid cuboid = new Cuboid(player.getWorld().getName(), user.getFirstPosition(), user.getSecondPosition());
                    regionStorage.createRegion(regionName, new Region(
                            regionName,
                            new ArrayList<>(),
                            new HashMap<>(),
                            cuboid
                    ));

                    userStorage.removeUser(player.getUniqueId());
                    player.sendMessage(Util.color("&7Region &e" + regionName + "&7 has been successfully created."));
                    return true;
                }

                case("delete"): {
                    if(args.length != 2) {
                        sender.sendMessage(Util.color("&cUsage: /region delete <name>"));
                        return false;
                    }

                    if(!sender.hasPermission("region.create")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    String regionName = args[1];
                    if(!regionStorage.isRegionExists(regionName)) {
                        sender.sendMessage(Util.color("&cThis region doesn't exist."));
                        return false;
                    }

                    regionStorage.removeRegion(regionName);
                    sender.sendMessage(Util.color("&7Region &e" + regionName + "&7has been successfully deleted."));
                    regionStorage.deleteRegion(regionName);
                    return true;
                }

                case("wand"):{
                    if(args.length != 1) {
                        sender.sendMessage(Util.color("&cUsage: /region wand"));
                        return false;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.color("&cYou must be a player to execute this command!"));
                        return false;
                    }

                    Player player = (Player) sender;
                    if(!player.hasPermission("region.create")) {
                        player.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    player.getInventory().addItem(new ItemBuilder(Material.STICK).setDisplayName(Util.color("&e&lRegion Wand")).build());
                    player.sendMessage(Util.color("&eYou received a Region Wand."));
                    return true;
                }
                case("add"): {
                    if(args.length != 3) {
                        sender.sendMessage(Util.color("&cUsage: /region add <name> <player>"));
                        return false;
                    }

                    if(!sender.hasPermission("region.add")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    String regionName = args[1];
                    if(!regionStorage.isRegionExists(regionName)) {
                        sender.sendMessage(Util.color("This region doesn't exist."));
                        return false;
                    }

                    OfflinePlayer offlinePlayer = sender.getServer().getOfflinePlayer(args[2]);

                    Region region = regionStorage.getRegion(regionName);
                    if(region.getMembers().contains(offlinePlayer.getUniqueId())){
                        sender.sendMessage(Util.color("This player is already on the whitelist."));
                        return false;
                    }

                    region.addMembers(offlinePlayer.getUniqueId());
                    sender.sendMessage(Util.color("&e" + offlinePlayer.getName() + "&7is now whitelisted in &e" + regionName + "&e."));
                    regionStorage.updateRegion(region);
                    return true;
                }
                case("remove"): {
                    if(args.length != 3) {
                        sender.sendMessage(Util.color("&cUsage: /region remove <name> <player>"));
                        return false;
                    }

                    if(!sender.hasPermission("region.remove")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    String regionName = args[1];
                    if(!regionStorage.isRegionExists(regionName)){
                        sender.sendMessage(Util.color("&cThis region doesn't exist."));
                        return false;
                    }

                    OfflinePlayer offlinePlayer = sender.getServer().getOfflinePlayer(args[2]);

                    Region region = regionStorage.getRegion(regionName);
                    if(!region.getMembers().contains(offlinePlayer.getUniqueId())) {
                        sender.sendMessage(Util.color("&cThis player is not on the whitelist."));
                        return false;
                    }

                    region.removeMembers(offlinePlayer.getUniqueId());
                    sender.sendMessage(Util.color("&e" + offlinePlayer.getName() + "&7is no longer whitelisted in &e" + regionName + "&7."));
                    regionStorage.updateRegion(region);
                    return true;
                }
                case("whitelist"): {
                    if(args.length != 2) {
                        sender.sendMessage(Util.color("&cUsage: /region whitelist <name>"));
                        return false;
                    }

                    if(!sender.hasPermission("region.whitelist")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    String regionName = args[1];
                    if(!regionStorage.isRegionExists(regionName)) {
                        sender.sendMessage(Util.color("This region doesn't exist."));
                        return false;
                    }

                    Region region = regionStorage.getRegion(regionName);
                    sender.sendMessage(Util.color("&7List of all whitelisted players for &e" + regionName + "&7:"));
                    for(UUID memberUUID : region.getMembers()) {
                        OfflinePlayer member = sender.getServer().getOfflinePlayer(memberUUID);
                        sender.sendMessage(Util.color("&8- &e" + member.getName()));
                    }
                    return true;
                }
                case("flag"): {
                    if(args.length != 4){
                        sender.sendMessage(Util.color("&cUsage: /region flag <name> <flag> <state>"));
                        return false;
                    }

                    if(!sender.hasPermission("region.flag")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    String regionName = args[1];
                    if(!regionStorage.isRegionExists(regionName)) {
                        sender.sendMessage(Util.color("&cThis region doesn't exist."));
                        return false;
                    }

                    Region region = regionStorage.getRegion(regionName);
                    Flag flag = flagStorage.getFlag(args[2]);

                    if(flag == null) {
                        sender.sendMessage(Util.color("&cThis flag doesn't exist."));
                        return false;
                    }

                    String stateString = args[3];
                    if(!stateString.equalsIgnoreCase("everyone") && !stateString.equalsIgnoreCase("whitelist") && !stateString.equalsIgnoreCase("none")) {
                        sender.sendMessage(Util.color("&cInvalid state. Use Everyone, Whitelist or None."));
                        return false;
                    }

                    FlagMode state = FlagMode.valueOf(stateString.toUpperCase());
                    region.addFlag(flag.getName(), state);
                    sender.sendMessage(Util.color("&7Successfully set &e" + flag.getName() + "&7 to &e" + stateString + "&7 in region &e" + regionName + "&7."));
                    regionStorage.updateRegion(region);

                    return true;
                }
                default: {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.color("&cYou must be a player to execute this command!"));
                        return false;
                    }
                    if(!sender.hasPermission("region.menu")) {
                        sender.sendMessage(Util.color("&cYou don't have permission to use this command."));
                        return false;
                    }

                    Player player = (Player) sender;
                    String regionName = args[0];

                    if(!regionStorage.isRegionExists(regionName)) {
                        player.sendMessage(Util.color("&cThis region doesn't exist."));
                        return false;
                    }

                    EditUser user = userStorage.getUser(player.getUniqueId());
                    Region region = regionStorage.getRegion(regionName);
                    RegionInventory.getInventory(user, region, regionStorage).open(player);

                    return true;
                }
            }
        }
    }
}
