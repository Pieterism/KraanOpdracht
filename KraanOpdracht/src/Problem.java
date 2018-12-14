import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Problem {

    private List<Job> inputJobSequence;
    private List<Job> outputJobSequence;
    private final List<Gantry> gantries;
    private final List<Slot> slots;
    public static Slot OUTPUT_SLOT, INPUT_SLOT;

    private List<Slot> availableSlots;
    private List<Slot> occupiedSlots;
    private List<Move> executedMoves;

    Problem(List<Job> inputJobSequence,
            List<Job> outputJobSequence,
            List<Gantry> gantries,
            List<Slot> slots) {
        this.inputJobSequence = inputJobSequence;
        this.outputJobSequence = outputJobSequence;
        this.gantries = gantries;
        this.slots = slots;

        availableSlots = new ArrayList<>();
        occupiedSlots = new ArrayList<>();
        executedMoves = new ArrayList<>();
        setInputOutputSlots();
        createSlotField();
    }

    //TODO
    //oplossing: eerst input doorlopen, achteraf output behandelen + uitprinten als csv
    public void solve() {

        Gantry input_gantry = gantries.get(0);
        Gantry output_gantry = gantries.get(0);

        Move start = new Move(input_gantry, input_gantry.getStartX(), input_gantry.getStartY());
        executedMoves.add(start);

        for (Job j : inputJobSequence) {
            Item item = j.getItem();
            INPUT_SLOT.setItem(item);
            inputItem(input_gantry, availableSlots.get(0));
        }

        for (Job j : outputJobSequence) {
            Item item = j.getItem();
            outputItem(output_gantry, getSlot(item));
        }
    }

    // genereert linked list voorstelling van de slots
    public void createSlotField() {
        slots.forEach(slot -> {
            slots.forEach(slot1 -> {
                if(slot1.isParent(slot)) slot.addParentSlot(slot);
            });
            if (slot.isAvailable()) this.availableSlots.add(slot);
            else this.occupiedSlots.add(slot);
        });
    }

    //loops all slots and determines if occupied or not
    public void updateSlots() {
        for (Slot s : slots) {
            if (s.isAvailable()) {
                this.availableSlots.add(s);
            } else {
                this.occupiedSlots.add(s);
            }
        }
    }

    //geeft slot van bepaald item terug
    public Slot getSlot(Item item) {
        return occupiedSlots.stream()
                .filter(slot -> slot.getItem() == item)
                .findFirst()
                .orElse(null);
    }

    //returns closest available slot
    public Slot getClosestAvailableSlot(Slot s) {
        return s.getClosestSlot(availableSlots);
    }

    //Kraan gantry verplaatsen naar een bepaald slot
    public void moveGantry(Gantry gantry, Slot naar) {
        executedMoves.add(gantry.moveTo(naar.getCenterX(), naar.getCenterY()));
    }

    //ELEMENTARY OPERATIONS
    //Methode om gewenst item op de pikken uit slot s
    public void pickupItemFromSlot(Gantry gantry, Slot slot) {
        if (slot.isTopSlot()) {
            executedMoves.add(new Move(gantry, getClosestAvailableSlot(slot).getCenterX(), getClosestAvailableSlot(slot).getCenterY()));
        }else{

        }


    }


    //Methode om item in gantry te plaatsen in slot
    public void placeItemInSlot(Gantry gantry, Slot slot) {

        executedMoves.add(gantry.placeItem(slot));

        if (!slot.equals(OUTPUT_SLOT)) {
            occupiedSlots.add(slot);
            availableSlots.remove(slot);
            availableSlots.addAll(slot.getParentSlots());
        }
    }

    //Methode op item te verplaatsen van slot s1 naar slot s2: move + pickup + move + place
    public void moveItem(Gantry gantry, Slot s1, Slot s2) {
        moveGantry(gantry, s1);
        pickupItemFromSlot(gantry, s1);
        moveGantry(gantry, s2);
        placeItemInSlot(gantry, s2);
        updateSlots();
    }

    public void moveParents(Gantry gantry, Slot slot) {
        for (Slot parent : slot.getParentSlots()) {
            if (parent.hasItem()) {
                moveParents(gantry, parent);
            }
            //moveItem(gantry, parent, getAvailableSlot(slot.getAllSlotsAbove()));
            moveGantry(gantry, slot);
        }
    }

    private Slot getAvailableSlot(List<Slot> parentSlots) {
        for (Slot s : availableSlots) {
            if (!parentSlots.contains(s)) {
                return s;
            }
        }
        return null;
    }

    //Item oppikken op INPUT_SLOT en verplaatsen naar first available slot
    public void inputItem(Gantry gantry, Slot s) {
        moveItem(gantry, INPUT_SLOT, availableSlots.get(0));
    }

    //Item oppikken in slot s, en verplaatsen naar OUTPUT_SLOT
    public void outputItem(Gantry gantry, Slot s) {
        if (s.isTopSlot()) {
            moveItem(gantry, s, OUTPUT_SLOT);
        } else {
            moveParents(gantry, s);

        }

    }

    public void printMoves() {
        for (Move m : executedMoves) {
            System.out.println(m.toString());
        }
    }

    public void createCSV(String OUTPUT_FILENAME) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_FILENAME));
        String header = "gID;T;x;y;itemInCraneID\n";

        pw.write(header);

        for (Move m : executedMoves) {
            pw.write(m.toString());
            pw.write("\n");
        }

        pw.close();

    }

    public void setInputOutputSlots() {
        for (Slot s : slots) {
            if (s.getType() == Slot.SlotType.INPUT) {
                INPUT_SLOT = s;
            }
            if (s.getType() == Slot.SlotType.OUTPUT) {
                OUTPUT_SLOT = s;
            }
        }
    }

    public void clearExecutedMoves() {
        this.executedMoves.clear();
    }


}
