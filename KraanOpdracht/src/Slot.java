import java.util.ArrayList;
import java.util.List;

public class Slot {

    private final int id;
    private final int centerX, centerY, xMin, xMax, yMin, yMax, z;
    private Item item;
    private final SlotType type;
    private List<Slot> parentSlots;
    private List<Slot> childSlots;

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
        parentSlots = new ArrayList<>();
        childSlots = new ArrayList<>();
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

    //Controleert slot bovenaan (en dus beschikbaar) ligt
    //TODO: check
    public boolean isTopSlot() {
        return parentSlots.stream().allMatch(parent -> parent.getItem() == null || parent.equals(null));


    }

    //Controleren of een slot beschikbaar is om container te plaatsen
    // TODO: Check
    public boolean isAvailable() {
        //wanneer slot op onderste laag ligt mag er altijd container worden geplaatst indien vrij
        if (this.isBottomSlot()) {
            if (this.isStorageSlot() && this.item == null) {
                return true;
            }
            //slot ligt niet onderaan
        } else {
            if (childSlots.size() == 2) {
                if (childSlots.stream().allMatch(child -> !child.isAvailable()) && this.isTopSlot()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Slot %d (%d,%d,%d)", id, centerX, centerY, z);
    }

    //True if parentslot is parent from this slot
    public boolean isParent(Slot parentslot) {
        if (parentslot.getZ() == this.getZ() + 1 && this.getCenterY() == parentslot.getCenterY()) {
            if (parentslot.getXMin() == this.getCenterX() || parentslot.getXMax() == this.getCenterX()) {
                return true;
            }
        }
        return false;
    }

    public static enum SlotType {
        INPUT,
        OUTPUT,
        STORAGE
    }
}
