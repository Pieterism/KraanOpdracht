
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Problem {

    private final List<Job> inputJobSequence;
    private final List<Job> outputJobSequence;
    private final List<Gantry> gantries;
    private final List<Slot> slots;

    private Slot OUTPUT_SLOT, INPUT_SLOT;
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

    //linked-list voorstellig van slot veld
    private void createSlotField() {
        slots.forEach(this::setParents);
        slots.forEach(slot -> {
            if (slot.isAvailable()) this.availableSlots.add(slot);
            else this.occupiedSlots.add(slot);
        });
    }

    private void setParents(Slot s) {
        slots.forEach(slot -> {
            if(s.isParent(slot)){
                s.addParentSlot(slot);
            }
        });
    }

    //geeft slot van bepaald item terug
    public Slot getSlot(Item item) {
        return occupiedSlots.stream()
                .filter(slot -> slot.getItem() == item)
                .findFirst()
                .orElse(null);
    }

    //Kraan gantry verplaatsen naar een bepaald slot
    public void moveGantry(Gantry gantry, Slot naar) {
        executedMoves.add(gantry.moveTo(naar.getCenterX(), naar.getCenterY()));
    }

    //Methode om gewenst item op de pikken uit slot s
    //TODO rekening mee houden dat slot available moet zijn om item te kunnen oppikken! (eventueel andere items verplaatsen)
    public void pickupItem(Gantry gantry, Slot s) {
        if (s.isTopSlot()) {
            executedMoves.add(gantry.pickupItem(s));
        } else {
            while (!s.getParentSlots().isEmpty()) {
                pickupItem(gantry, s.getParentSlots().get(0));
            }
            executedMoves.add(gantry.pickupItem(s));
        }
    }

    //Methode om item in gantry te plaatsen in slot s
    public void placeItem(Gantry gantry, Slot s) {
        executedMoves.add(gantry.placeItem(s));
        disableSlot(s);
    }

    //Methode op item te verplaatsen van slot s1 naar slot s2
    public void moveItem(Gantry gantry, Slot s1, Slot s2) {
        moveGantry(gantry, s1);
        pickupItem(gantry, s1);
        enableSlot(s1);
        moveGantry(gantry, s2);
        placeItem(gantry, s2);
    }

    //Item oppikken op INPUT_SLOT en verplaatsen naar first available slot
    public void inputItem(Gantry gantry, Slot s) {
        moveGantry(gantry, INPUT_SLOT);
        pickupItem(gantry, INPUT_SLOT);
        moveGantry(gantry, s);
        placeItem(gantry, s);
        disableSlot(s);
    }

    //Item oppikken in slot s, en verplaatsen naar OUTPUT_SLOT
    public void outputItem(Gantry gantry, Slot s) {
        Optional.ofNullable(s)
                .ifPresent(slot -> moveItem(gantry, slot, OUTPUT_SLOT));
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

    public Move getLastMove() {
        Move m = executedMoves.get(executedMoves.size() - 1);
        return m;
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

    //add slot to occupied slots list and remove from available slots list
    public void disableSlot(Slot s) {
        availableSlots.remove(s);
        occupiedSlots.add(s);
    }

    //add slot to available slots list and remove from occupied slots list
    public void enableSlot(Slot s) {
        availableSlots.add(s);
        s.removeItem();
        occupiedSlots.remove(s);
    }

    public void clearExecutedMoves() {
        this.executedMoves.clear();
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
            INPUT_SLOT.putItem(item);
            inputItem(input_gantry, availableSlots.get(0));
        }

        for (Job j : outputJobSequence) {
            Item item = j.getItem();
            outputItem(output_gantry, getSlot(item));
        }
    }
}
