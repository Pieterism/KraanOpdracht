import java.util.List;

public class Slot {

    private final int id;
    private final int centerX, centerY, xMin, xMax, yMin, yMax, z;
    private Item item;
    private final SlotType type;
    private Slot parentSlots;
    private Slot childSlots;

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

    public void putItem(Item item) {
        this.item = item;
    }

    public SlotType getType() {
        return type;
    }

    public List<Slot> getParentSlots() {
        return parentSlots;
    }

    public void addParentSlot(Slot parentSlot) {
        this.parentSlots.add(parentSlot);
        parentSlot.addChildSlot(this);
    }

    public List<Slot> getChildSlots() {
        return childSlots;
    }

    public void addChildSlot(Slot childSlot) {
        this.childSlots.add(childSlot);
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

    public boolean isBottomSlot() {
        if (this.z == 0) {
            return true;
        } else return false;
    }

    //Controleert of dit slot vrij bovenaan ligt
    //TODO
    public boolean isTopSlot() {
        for (Slot parent : this.getParentSlots()) {
            if (parent == null || parent.getItem() == null) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //Controleren of een slot beschikbaar is om container te plaatsen
    // TODO
    public boolean isAvailable() {
        if (this.isBottomSlot()) {
            if (this.isStorageSlot() && this.item == null) {
                return true;
            } else {
                return false;
            }
        } else {
            for (Slot parent : parentSlots) {
                if (parent.isStorageSlot() && parent.item == null) {
                    for (Slot child : parent.getChildSlots()) {
                        if (!child.isAvailable()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
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
