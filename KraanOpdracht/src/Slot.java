import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Slot {

    private final int id;
    private final int centerX, centerY, xMin, xMax, yMin, yMax, z;
    private Item item;
    private final SlotType type;
    private Set<Slot> parentSlots;
    private Set<Slot> childSlots;

    public Slot(int id, int centerX, int centerY, int xMin, int xMax, int yMin, int yMax, int z, SlotType type, Item item) {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.z = z;
        this.item = item;
        this.type = type;
        parentSlots = new HashSet<>();
        childSlots = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getZ() {
        return z;
    }

    public int getXMin() {
        return xMin;
    }

    public int getXMax() {
        return xMax;
    }

    public int getYMin() {
        return yMin;
    }

    public int getYMax() {
        return yMax;
    }

    public Item getItem() {
        return item;
    }

    public void removeItem() {
        this.item = null;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public SlotType getType() {
        return type;
    }

    public void addChildSlot(Slot childSlot) {
        this.childSlots.add(childSlot);
    }

    public void addParentSlot(Slot parentSlot) {
        this.parentSlots.add(parentSlot);
        parentSlot.addChildSlot(this);
    }

    public Set<Slot> getChildSlots() {
        return childSlots;
    }

    public Set<Slot> getParentSlots() {
        return parentSlots;
    }

    public boolean hasItem() {
        if (this.item == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isInputSlot() {
        return type == SlotType.INPUT;
    }

    public boolean isOutputSlot() {
        return type == SlotType.OUTPUT;
    }

    public boolean isStorageSlot() {
        return type == SlotType.STORAGE;
    }

    public boolean isBottomLayerSlot() {
        if (this.z == 0) {
            return true;
        } else return false;
    }

    public boolean isTopSlot() {
        if (parentSlots.isEmpty() || parentSlots.stream().allMatch(slot -> !slot.hasItem())) return true;
        else return false;
    }

    //Controleren of een slot beschikbaar is om container te plaatsen
    public boolean isAvailable() {
        if (this.isTopSlot() && !this.hasItem()) return true;
        else parentSlots.forEach(slot -> slot.isAvailable());
        return false;
    }

    //Returns all slots interfering with this slot, empty and non empty
    public Set<Slot> getOccupiedParentSlots() {
        Set<Slot> result = new HashSet<>();
        this.parentSlots.forEach(slot -> {
            if (slot.isTopSlot()) {
                if (slot.hasItem()) {
                    result.add(slot);
                }
            } else {
                if (slot.hasItem()) result.add(slot);
                else slot.getOccupiedParentSlots();
            }

        });
        return result;
    }

    //returns all slots where you cannot place the item if you just picked it up at this slot
    public Set<Slot> toDisableSlotsAfterPickup() {
        Set<Slot> result = new HashSet<>();
        result.add(this);
        this.parentSlots.forEach(slot -> {
            if (slot.isTopSlot()) {
                result.add(slot);
            } else {
                result.add(slot);
                toDisableSlotsAfterPickup();
            }
        });
        return result;

    }

    //return all slots that should be enabled when item is placed in this slot
    public Set<Slot> toEnableSlotsAfterPlace() {
        return this.parentSlots;
    }

    //returns true if slot is a parentslot of this
    public boolean isParent(Slot parentslot) {
        if (parentslot.getZ() == this.getZ() + 1 && this.getCenterY() == parentslot.getCenterY()) {
            if (parentslot.getXMin() == this.getCenterX() || parentslot.getXMax() == this.getCenterX()) {
                return true;
            }
        }
        return false;
    }

    public Slot getClosestSlot(List<Slot> slots) {
        Slot result = null;
        double distance = 1000;
        for (Slot slot : slots) {
            double d = Point2D.distance(slot.centerX, slot.centerY, this.centerX, this.centerY);
            if (d < distance) {
                result = slot;
                distance = d;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Slot %d (%d,%d,%d)", id, centerX, centerY, z);
    }

    public static enum SlotType {
        INPUT,
        OUTPUT,
        STORAGE
    }
}
