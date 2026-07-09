package model;

import java.time.LocalTime;

public class Slot {
    private final int slotId;
    private final LocalTime time;

    public Slot(int slotId, LocalTime time) {
        this.slotId = slotId;
        this.time = time;
    }

    public int getSlotId() {
        return slotId;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time.toString();
    }
}
