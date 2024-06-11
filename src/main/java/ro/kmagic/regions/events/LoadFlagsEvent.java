package ro.kmagic.regions.events;

import ro.kmagic.regions.objects.Flag;

public class LoadFlagsEvent extends RegionEvent {

    private final String id;
    private final Flag flag;

    public LoadFlagsEvent(String id, Flag flag) {
        this.id = id;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public Flag getFlag() {
        return flag;
    }

}
