package ro.kmagic.regions.listeners;

import ro.kmagic.regions.enums.EditMode;
import ro.kmagic.regions.enums.FlagMode;
import ro.kmagic.regions.inventories.RegionInventory;
import ro.kmagic.regions.managers.RegionManager;
import ro.kmagic.regions.objects.BlockPosition;
import ro.kmagic.regions.objects.EditUser;
import ro.kmagic.regions.objects.Region;
import ro.kmagic.regions.storage.RegionStorage;
import ro.kmagic.regions.storage.UserStorage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import ro.kmagic.regions.utils.Util;

import java.util.List;

public class PlayerListener implements Listener {

    private final UserStorage userStorage;
    private final RegionStorage regionStorage;
    private final RegionManager regionManager;

    public PlayerListener(UserStorage userStorage, RegionStorage regionStorage, RegionManager regionManager) {
        this.userStorage = userStorage;
        this.regionStorage = regionStorage;
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        userStorage.addUser(event.getPlayer().getUniqueId(), new EditUser(event.getPlayer().getUniqueId(), EditMode.NONE));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(userStorage.containsUser(event.getPlayer().getUniqueId())){
            userStorage.removeUser(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

        Player player = event.getPlayer();
        if(!player.hasPermission("region.create")) return;
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().equalsIgnoreCase(Util.color("&e&lRegion Wand"))) return;
        EditUser user = userStorage.getUser(player.getUniqueId());

        event.setCancelled(true);
        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            player.sendMessage(Util.color("&eFirst position set."));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            user.setFirstPosition(new BlockPosition(event.getClickedBlock().getLocation()));
        } else {
            player.sendMessage(Util.color("&eSecond position set."));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            user.setSecondPosition(new BlockPosition(event.getClickedBlock().getLocation()));
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!userStorage.containsUser(player.getUniqueId())) return;

        EditUser user = userStorage.getUser(player.getUniqueId());
        if(user.getEditMode().equals(EditMode.NONE)) return;

        if(user.getRegionId() == null) return;
        Region region = regionStorage.getRegion(user.getRegionId());
        if(region == null) return;

        String message = event.getMessage();
        if(message.equalsIgnoreCase("cancel")){
            player.sendMessage(Util.color("&cRegion name change cancelled."));
            user.setRegionId(null);
            user.setEditMode(EditMode.NONE);
            RegionInventory.getInventory(user, region, regionStorage).open(player);
            return;
        }

        event.setCancelled(true);
        switch (user.getEditMode()) {
            case NAME:
                if(message.contains(" ")){
                    player.sendMessage(Util.color("&c§7Region name cannot contain spaces."));
                    return;
                }

                if(regionStorage.isRegionExists(message)){
                    player.sendMessage(Util.color("&cRegion already exists."));
                    return;
                }

                regionStorage.deleteRegion(region.getName());
                region.setName(message);
                regionStorage.updateRegion(region);
                player.sendMessage(Util.color("&7Region name set to &e" + event.getMessage() + "&7."));

                user.setRegionId(null);
                user.setEditMode(EditMode.NONE);
                RegionInventory.getInventory(user, region, regionStorage).open(player);
                break;
            case ADD_WHITELIST:
                OfflinePlayer addedPlayer = Bukkit.getOfflinePlayer(event.getMessage());
                if(!addedPlayer.hasPlayedBefore()){
                    player.sendMessage(Util.color("&Player not found."));
                    return;
                }

                if(region.getMembers().contains(addedPlayer.getUniqueId())){
                    player.sendMessage(Util.color("&cPlayer is already in the whitelist."));
                    return;
                }

                region.addMembers(addedPlayer.getUniqueId());
                player.sendMessage(Util.color("&cAdded player to the whitelist."));

                user.setRegionId(null);
                user.setEditMode(EditMode.NONE);
                regionStorage.updateRegion(region);
                RegionInventory.getInventory(user, region, regionStorage).open(player);
                break;
            case REMOVE_WHITELIST:
                OfflinePlayer removedPlayer = Bukkit.getOfflinePlayer(event.getMessage());
                if(!removedPlayer.hasPlayedBefore()){
                    player.sendMessage(Util.color("&cPlayer not found."));
                    return;
                }

                if(!region.getMembers().contains(removedPlayer.getUniqueId())){
                    player.sendMessage(Util.color("&cPlayer is not in the whitelist."));
                    return;
                }

                region.removeMembers(removedPlayer.getUniqueId());
                player.sendMessage(Util.color("&7Player &e" + player.getName() + "&7 was removed from the whitelist."));

                user.setRegionId(null);
                user.setEditMode(EditMode.NONE);
                regionStorage.updateRegion(region);
                RegionInventory.getInventory(user, region, regionStorage).open(player);
                break;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(player.hasPermission("region.bypass")) return;
        List<Region> regions = regionManager.getRegionsAt(block.getLocation().getWorld().getName(), new BlockPosition(block.getLocation()));
        if(regions.isEmpty()) return;

        for(Region region : regions){
            FlagMode flagMode = regionManager.getFlagValue(region, "block-break");
            if(flagMode.equals(FlagMode.EVERYONE)) return;
            if(flagMode.equals(FlagMode.NONE)){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot break blocks here."));
                return;
            }
            if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot break blocks here."));
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(player.hasPermission("region.bypass")) return;
        List<Region> regions = regionManager.getRegionsAt(block.getLocation().getWorld().getName(), new BlockPosition(block.getLocation()));
        if(regions.isEmpty()) return;

        for(Region region : regions){
            FlagMode flagMode = regionManager.getFlagValue(region, "block-place");
            if(flagMode.equals(FlagMode.EVERYONE)) return;
            if(flagMode.equals(FlagMode.NONE)){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot place blocks here."));
                return;
            }
            if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot place blocks here."));
                return;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_BLOCK)) return;

        Block block = event.getClickedBlock();
        if(block == null) return;
        if(!isInteractable(block)) return;

        Location location = event.getClickedBlock().getLocation();

        if(player.hasPermission("region.bypass")) return;
        List<Region> regions = regionManager.getRegionsAt(location.getWorld().getName(), new BlockPosition(location));
        if(regions.isEmpty()) return;

        for(Region region : regions){
            FlagMode flagMode = regionManager.getFlagValue(region, "interact");
            if(flagMode.equals(FlagMode.EVERYONE)) return;
            if(flagMode.equals(FlagMode.NONE)){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
            if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        Location location = entity.getLocation();

        if(player.hasPermission("region.bypass")) return;
        List<Region> regions = regionManager.getRegionsAt(location.getWorld().getName(), new BlockPosition(location));
        if(regions.isEmpty()) return;

        for(Region region : regions){
            FlagMode flagMode = regionManager.getFlagValue(region, "interact");
            if(flagMode.equals(FlagMode.EVERYONE)) return;
            if(flagMode.equals(FlagMode.NONE)){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
            if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
        }
    }

    @EventHandler
    public void onOpenContainer(SignChangeEvent event){
        Player player = event.getPlayer();

        Location location = event.getBlock().getLocation();

        if(player.hasPermission("region.bypass")) return;
        List<Region> regions = regionManager.getRegionsAt(location.getWorld().getName(), new BlockPosition(location));
        if(regions.isEmpty()) return;

        for(Region region : regions){
            FlagMode flagMode = regionManager.getFlagValue(region, "interact");
            if(flagMode.equals(FlagMode.EVERYONE)) return;
            if(flagMode.equals(FlagMode.NONE)){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
            if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                event.setCancelled(true);
                player.sendMessage(Util.color("&cYou cannot interact here."));
                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        if(damagerEntity instanceof Player){
            Player player = (Player) damagerEntity;

            if(player.hasPermission("region.bypass")) return;
            List<Region> regions = regionManager.getRegionsAt(entity.getLocation().getWorld().getName(), new BlockPosition(entity.getLocation()));
            if(regions.isEmpty()) return;

            for(Region region : regions){
                FlagMode flagMode = regionManager.getFlagValue(region, "entity-damage");
                if(flagMode.equals(FlagMode.EVERYONE)) return;
                if(flagMode.equals(FlagMode.NONE)){
                    event.setCancelled(true);
                    player.sendMessage(Util.color("&cYou cannot attack entities here."));
                    return;
                }
                if(flagMode.equals(FlagMode.WHITELIST) && !region.getMembers().contains(player.getUniqueId())){
                    event.setCancelled(true);
                    player.sendMessage(Util.color("&cYou cannot attack entities here."));
                    return;
                }
            }
        }
    }

    public static boolean isInteractable(Block block) {
        Material type = block.getType();
        boolean interactable = type.isInteractable();
        if (!interactable)
            return false;
        switch (type) {
            case OAK_DOOR:
            case IRON_DOOR:
            case OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case CRIMSON_DOOR:
            case WARPED_DOOR:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case WARPED_FENCE_GATE:
            case LEVER:
            case STONE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case BIRCH_BUTTON:
            case JUNGLE_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case CRIMSON_BUTTON:
            case WARPED_BUTTON:
            case TRIPWIRE_HOOK:
            case CHEST:
            case FURNACE:
            case CRAFTING_TABLE:
            case CRAFTER:
            case TRAPPED_CHEST:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case BARREL:
            case BREWING_STAND:
            case BEACON:
            case ENCHANTING_TABLE:
            case ENDER_CHEST:
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
            case GRINDSTONE:
            case SMITHING_TABLE:
            case STONECUTTER:
            case LECTERN:
            case LOOM:
            case CARTOGRAPHY_TABLE:
            case FLETCHING_TABLE:
            case SMOKER:
            case BLAST_FURNACE:
            case CAMPFIRE:
            case SOUL_CAMPFIRE:
            case BEEHIVE:
            case BEE_NEST:
            case COMPOSTER:
            case BELL:
            case JUKEBOX:
            case NOTE_BLOCK:
            case END_PORTAL_FRAME:
            case END_GATEWAY:
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
                return true;
            default:
                return false;
        }
    }
}
