public class Slot {

    private final int id;
    private final int centerX, centerY, xMin, xMax, yMin, yMax, z;
    private Item item;
    private final SlotType type;
    private Slot parentSlot;
    private Slot childSlot;

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

    public void putItem(Item item) {
        this.item = item;
    }

    public SlotType getType() {
        return type;
    }

    public Slot getParentSlot() {
        return parentSlot;
    }

    public void setParentSlot(Slot parentSlot) {
        this.parentSlot = parentSlot;
        parentSlot.setChildSlot(this);
    }

    public Slot getChildSlot() {
        return childSlot;
    }

    public void setChildSlot(Slot childSlot) {
        this.childSlot = childSlot;
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

    //Controleren of een slot beschikbaar is om container te plaatsen
    public boolean isAvailable() {
        if (this.isBottomSlot()) {
            if (this.isStorageSlot() && this.item == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (this.isStorageSlot() && this.item == null && !this.getChildSlot().isAvailable()) {
                return true;
            } else {
                return false;
            }
        }
    }

    //Controleren of er items in bovenliggende slots zitten
    public boolean hasAbove() {
        if (this.getParentSlot().getItem() == null) {
            return false;
        } else {
            return true;
        }
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
