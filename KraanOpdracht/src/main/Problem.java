package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Problem {

    private List<Job> inputJobSequence;
    private List<Job> outputJobSequence;
    private final List<Gantry> gantries;
    private final List<Slot> slots;
    public static Slot OUTPUT_SLOT, INPUT_SLOT;

    private List<Slot> availableSlots;
    private List<Slot> occupiedSlots;
    private List<Slot> tempDisabledSlots;
    private List<Slot> tempEnabledSlots;
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
        tempDisabledSlots = new ArrayList<>();
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
            inputItem(input_gantry, getClosestAvailableSlot(INPUT_SLOT));
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
                if (slot.isParent(slot1)) {
                    slot.addParentSlot(slot1);
                    slot1.addChildSlot(slot);
                }
            });
            if (slot.isAvailable()) this.availableSlots.add(slot);
            else this.occupiedSlots.add(slot);
        });
        setInputOutputSlots();
    }

    //loops all slots and determines if occupied or not
    public void updateSlots() {
        availableSlots.clear();
        occupiedSlots.clear();
        slots.forEach(slot -> {
            if (slot.hasItem()) occupiedSlots.add(slot);
            else availableSlots.add(slot);
        });
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
        List<Slot> tempAvailableSlots = new ArrayList(availableSlots);
        tempAvailableSlots.removeAll(tempDisabledSlots);
        tempAvailableSlots.remove(INPUT_SLOT);
        tempAvailableSlots.remove(OUTPUT_SLOT);
        return s.getClosestSlot(tempAvailableSlots);
    }

    //Kraan gantry verplaatsen naar een bepaald slot
    public void moveGantry(Gantry gantry, Slot naar) {
        executedMoves.add(gantry.moveTo(naar.getCenterX(), naar.getCenterY()));
    }

    //ELEMENTARY OPERATIONS
    //Methode om gewenst item op de pikken uit slot s
    public void pickupItemFromSlot(Gantry gantry, Slot slot) {
        if (!slot.isTopSlot()) System.out.println("ERROR SLOT NOT AVAILABLE PICKUP");
        executedMoves.add(gantry.pickupItem(slot));
        updateSlots();
    }

    //Methode om item in gantry te plaatsen in slot
    public void placeItemInSlot(Gantry gantry, Slot slot) {
        if (!slot.isAvailable() && !slot.equals(OUTPUT_SLOT)) System.out.println("ERROR SLOT NOT AVAILABLE PLACE");
        executedMoves.add(gantry.placeItem(slot));
        updateSlots();
    }

    //TODO
    //Methode op item te verplaatsen van slot s1 naar slot s2: move + pickup + move + place
    public void moveItem(Gantry gantry, Slot s1, Slot s2) {
        moveGantry(gantry, s1);
        pickupItemFromSlot(gantry, s1);
        moveGantry(gantry, s2);
        placeItemInSlot(gantry, s2);
        updateSlots();
    }

    //TODO
    public void inputItem(Gantry gantry, Slot s) {
        moveItem(gantry, INPUT_SLOT, availableSlots.get(0));
    }

    //main.Item oppikken in slot s, en verplaatsen naar OUTPUT_SLOT
    public void outputItem(Gantry gantry, Slot s) {
        if (s.getOccupiedParentSlots().isEmpty()) moveItem(gantry, s, OUTPUT_SLOT);
        else {
            tempDisabledSlots.addAll(s.getAllAboveSlots());
            tempDisabledSlots.add(s);
            moveParents(gantry, s.getParentSlots());
            moveItem(gantry, s, OUTPUT_SLOT);
        }
    }

    //TODO
    private void moveParents(Gantry gantry, Set<Slot> slots) {
        slots.forEach(parentslot -> {
            if (!parentslot.isTopSlot()) {
                moveParents(gantry, parentslot.getParentSlots());
            }
            moveItem(gantry, parentslot, getClosestAvailableSlot(parentslot));
        });
    }

    //TODO
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
