package ro.kmagic.regions.inventories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import ro.kmagic.regions.Regions;
import ro.kmagic.regions.enums.EditMode;
import ro.kmagic.regions.objects.Cuboid;
import ro.kmagic.regions.objects.EditUser;
import ro.kmagic.regions.objects.Region;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.kmagic.regions.utils.Util;

import java.util.List;

public class RegionInventory implements InventoryProvider {

    private final EditUser user;
    private final Region region;
    private final RegionStorage regionStorage;

    public RegionInventory(EditUser user, Region region, RegionStorage regionStorage){
        this.user = user;
        this.region = region;
        this.regionStorage = regionStorage;
    }

    public static SmartInventory getInventory(EditUser user, Region region, RegionStorage regionStorage){
        return SmartInventory.builder()
                .id("regionInventory")
                .provider(new RegionInventory(user, region, regionStorage) {
                })
                .size(5, 9)
                .title("Region")
                .manager(Regions.getInstance().getInventoryManager())
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {

        ItemStack rename = new ItemBuilder(Material.PAPER)
                .setDisplayName(Util.color("&eRename"))
                .setLore(Util.colorList(List.of(
                        "",
                        Util.color("&7Current Name: &e" + region.getName()),
                        "",
                        "&eClick to modify!"
                )))
                .build();

        ItemStack addWhitelist = new ItemBuilder(Material.LIME_DYE)
                .setDisplayName(Util.color("&eAdd Whitelist"))
                .setLore(List.of(
                        "",
                        Util.color("&eClick to add!")
                ))
                .build();

        ItemStack removeWhitelist = new ItemBuilder(Material.RED_DYE)
                .setDisplayName(Util.color("&eRemove Whitelist"))
                .setLore(List.of(
                        "",
                        Util.color("&eClick to remove!")
                ))
                .build();

        ItemStack redefineLocation = new ItemBuilder(Material.COMPASS)
                .setDisplayName(Util.color("&eRedefine"))
                .setLore(List.of(
                        "",
                        Util.color("&eClick to redefine!")
                ))
                .build();

        ItemStack flags = new ItemBuilder(Material.YELLOW_BANNER)
                .setDisplayName(Util.color("&eFlags"))
                .setLore(List.of(
                        "",
                        Util.color("&eClick to view flags!")
                ))
                .build();

        contents.set(1, 2, ClickableItem.of(rename, e -> {
            if(!player.hasPermission("region.create")) {
                player.sendMessage(Util.color("&cYou do not have permission to rename the region."));
                return;
            }

            player.sendMessage(Util.color("&cPlease enter the new name for the region."));
            user.setEditMode(EditMode.NAME);
            user.setRegionId(region.getName());
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));

        contents.set(1, 3, ClickableItem.of(addWhitelist, e -> {
            if(!player.hasPermission("region.add")){
                player.sendMessage(Util.color("&cYou do not have permission to add players to whitelist."));
                return;
            }

            player.sendMessage(Util.color("&7Please enter the name of the player you want to add to the whitelist."));
            user.setEditMode(EditMode.ADD_WHITELIST);
            user.setRegionId(region.getName());
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));

        contents.set(1, 4, ClickableItem.of(removeWhitelist, e -> {
            if(!player.hasPermission("region.remove")){
                player.sendMessage(Util.color("&cYou do not have permission to remove players from whitelist."));
                return;
            }

            player.sendMessage(Util.color("&7Please enter the name of the player you want to remove from the whitelist."));
            user.setEditMode(EditMode.REMOVE_WHITELIST);
            user.setRegionId(region.getName());
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));

        contents.set(1, 5, ClickableItem.of(redefineLocation, e -> {
            if(!player.hasPermission("region.create")){
                player.sendMessage(Util.color("&cYou do not have permission to redefine the location."));
                return;
            }

            if(user.getFirstPosition() == null || user.getSecondPosition() == null) {
                player.sendMessage(Util.color("&cPlease select the region first."));
                return;
            }
            region.setCuboid(new Cuboid(player.getWorld().getName(), user.getFirstPosition(), user.getSecondPosition()));
            player.sendMessage(Util.color("&eRegion location has been redefined."));
            regionStorage.updateRegion(region);
            user.setFirstPosition(null);
            user.setSecondPosition(null);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));

        contents.set(1, 6, ClickableItem.of(flags, e -> {
            if(!player.hasPermission("region.flag")){
                player.sendMessage(Util.color("&cYou do not have permission to change flags."));
                return;
            }

            FlagsInventory.getInventory(user, region, regionStorage).open(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));

        contents.set(4, 4, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&eHome").build(), e -> {
            player.performCommand("region");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 4);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
