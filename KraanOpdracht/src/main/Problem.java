package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Problem {

    private List<Job> inputJobSequence;
    private List<Job> outputJobSequence;
    private final List<Gantry> gantries;
    private final List<Slot> slots;
    public static Slot OUTPUT_SLOT, INPUT_SLOT;
    private boolean geschrankt;

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
        List<Job> restingOutputJobs = new ArrayList<>();

        if (this.gantries.size() == 1) {
            Gantry gantry = gantries.get(0);

            Move start = new Move(gantry, gantry.getStartX(), gantry.getStartY());
            executedMoves.add(start);

            inputJobSequence.forEach(job -> {
                Item item = job.getItem();
                INPUT_SLOT.setItem(item);
                if (getOutputJob(item) == null) inputItem(gantry, getClosestAvailableSlot(INPUT_SLOT));
                else inputItem(gantry, OUTPUT_SLOT);
            });
            outputJobSequence.forEach(job -> {
                Item item = job.getItem();
                if (getSlot(item) == null) {
                    restingOutputJobs.add(job);
                } else {
                    outputItem(gantry, getSlot(item));
                }
            });
        } else if (this.gantries.size() == 2) {
            Gantry input_gantry = gantries.get(0);
            Gantry output_gantry = gantries.get(1);

            Move input_start = new Move(input_gantry, input_gantry.getStartX(), input_gantry.getStartY());
            Move output_start = new Move(output_gantry, output_gantry.getStartX(), output_gantry.getStartY());
            executedMoves.add(input_start);
            executedMoves.add(output_start);
        }
    }

    private Job getOutputJob(Item item) {
        return outputJobSequence.stream().filter(job -> job.getItem() == item).findAny().orElse(null);
    }

    public void setGeschrankt(boolean b) {
        this.geschrankt = b;
    }

    // genereert linked list voorstelling van de slots
    public void createSlotField() {
        if (geschrankt) {
            slots.forEach(slot -> {
                slots.forEach(s -> {
                    if (slot.isParentGeschrankt(s)) {
                        slot.addParentSlot(s);
                        s.addChildSlot(slot);
                    }
                });
                if (slot.isAvailable()) this.availableSlots.add(slot);
                else this.occupiedSlots.add(slot);
            });
        } else {
            slots.forEach(slot -> {
                slots.forEach(s -> {
                    if (slot.isParentGestapeld(s)) {
                        slot.addParentSlot(s);
                        s.addChildSlot(slot);
                    }
                });
                if (slot.isAvailable()) this.availableSlots.add(slot);
                else this.occupiedSlots.add(slot);
            });
        }

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
        List<Slot> tempAvailableSlots = availableSlots.stream().filter(slot -> slot.occupiedChildSlots()).collect(Collectors.toList());
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

    //Methode op item te verplaatsen van slot s1 naar slot s2: move + pickup + move + place
    public void moveItem(Gantry gantry, Slot s1, Slot s2) {
        moveGantry(gantry, s1);
        pickupItemFromSlot(gantry, s1);
        moveGantry(gantry, s2);
        placeItemInSlot(gantry, s2);
        updateSlots();
    }

    public void inputItem(Gantry gantry, Slot s) {
        moveItem(gantry, INPUT_SLOT, availableSlots.get(0));
    }

    public void outputItem(Gantry gantry, Slot s) {
        tempDisabledSlots.clear();
        if (s.getOccupiedParentSlots().isEmpty()) moveItem(gantry, s, OUTPUT_SLOT);
        else {
            tempDisabledSlots.addAll(s.getAllAboveSlots());
            tempDisabledSlots.add(s);
            moveParents(gantry, s.getParentSlots());
            moveItem(gantry, s, OUTPUT_SLOT);
        }
    }

    private void moveParents(Gantry gantry, Set<Slot> slots) {
        slots.forEach(parentslot -> {
            if (!parentslot.isTopSlot()) {
                moveParents(gantry, parentslot.getParentSlots());
            }
            moveItem(gantry, parentslot, getClosestAvailableSlot(parentslot));
        });
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
        clearExecutedMoves();
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

    public List<Gantry> getGantries() {
        return this.gantries;
    }

}
