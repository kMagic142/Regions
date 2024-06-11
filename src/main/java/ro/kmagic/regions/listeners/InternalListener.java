package ro.kmagic.regions.listeners;

import ro.kmagic.regions.events.LoadFlagsEvent;
import ro.kmagic.regions.storage.FlagStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class InternalListener implements Listener {

    private final FlagStorage flagStorage;
    private final Logger logger;

    public InternalListener(FlagStorage flagStorage, Logger logger) {
        this.flagStorage = flagStorage;
        this.logger = logger;
    }

    @EventHandler
    public void onLoadFlags(LoadFlagsEvent event) {
        if(flagStorage.getFlagsMap().containsKey(event.getId())) {
            logger.warning("Flag " + event.getId() + " already exist.");
        }
        flagStorage.addFlag(event.getId(), event.getFlag());
        logger.info("Flag " + event.getId() + " loaded.");
    }

}
