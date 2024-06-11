package ro.kmagic.regions.inventories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import ro.kmagic.regions.Regions;
import ro.kmagic.regions.objects.EditUser;
import ro.kmagic.regions.objects.Region;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.kmagic.regions.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class RegionListInventory implements InventoryProvider {

    private final EditUser user;
    private final RegionStorage regionStorage;

    public RegionListInventory(EditUser user, RegionStorage regionStorage){
        this.user = user;
        this.regionStorage = regionStorage;
    }

    public static SmartInventory getInventory(EditUser user, RegionStorage regionStorage){
        return SmartInventory.builder()
                .id("regionListInventory")
                .provider(new RegionListInventory(user, regionStorage) {})
                .size(5, 9)
                .title("Region List")
                .manager(Regions.getInstance().getInventoryManager())
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ArrayList<Region> regions = new ArrayList<>(regionStorage.getRegionsMap().values());
        ClickableItem[] items = new ClickableItem[regions.size()];

        for (int i = 0; i < regions.size(); i++){
            Region region = regions.get(i);
            ItemStack itemStack = new ItemBuilder(Material.BOOK)
                    .setDisplayName(region.getName())
                    .setLore(Util.colorList(List.of(
                          "",
                            "&7Members: &f" + region.getMembers().size(),
                            "&7Changed Flags: &f" + region.getFlags().size(),
                            "&7Location: &f" + region.getCuboid().getLocationString(),
                            "",
                            "&7Click to edit this region"
                    )))
                    .build();
            items[i] = ClickableItem.of(itemStack, e -> RegionInventory.getInventory(user, region, regionStorage).open(player));
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(10);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 2)
                .blacklist(1, 7)
                .blacklist(1, 8)
                .blacklist(2, 0)
                .blacklist(2, 1));

        if(!pagination.isFirst()){
            contents.set(4, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&cBack").build(), e -> {
                RegionListInventory.getInventory(user, regionStorage).open(player, pagination.previous().getPage());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 0);
            }));
        }

        if(!pagination.isLast()){
            contents.set(4, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&cNext").build(), e -> {
                RegionListInventory.getInventory(user, regionStorage).open(player, pagination.next().getPage());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4, 8);
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
