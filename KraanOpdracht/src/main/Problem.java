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
        tempDisabledSlots = new ArrayList<>();
        setInputOutputSlots();
        createSlotField();
    }

    //oplossing: eerst input doorlopen, achteraf output behandelen + uitprinten als csv
    public void solve() {
        if (this.gantries.size() == 1) {
            Gantry gantry = gantries.get(0);
            for (Job outputjob : outputJobSequence) {
                Item item = outputjob.getItem();
                if (getSlot(item) == null) {
                    //Item zit nog niet in slotfield dus eerst inputs overlopen tot en met item.
                    for (int i = inputJobSequence.size(); i > 0; i--) {
                        Job inputjob = inputJobSequence.get(0);
                        if (inputjob.getItem() == item) {
                            INPUT_SLOT.setItem(inputjob.getItem());
                            inputItem(gantry, OUTPUT_SLOT);
                            inputJobSequence.remove(inputjob);
                            break;
                        } else {
                            INPUT_SLOT.setItem(inputjob.getItem());
                            inputItem(gantry, availableSlots.get(0));
                            inputJobSequence.remove(inputjob);
                        }
                    }
                } else outputItem(gantry, getSlot(item));
            }

            inputJobSequence.forEach(inputjob -> {
                INPUT_SLOT.setItem(inputjob.getItem());
                inputItem(gantry, availableSlots.get(0));
            });


        }
        else if (this.gantries.size() == 2) {
            Gantry input_gantry = gantries.get(0);
            Gantry output_gantry = gantries.get(1);

            //startpositions
            moveGantry(input_gantry, new Slot(input_gantry.getStartX(), input_gantry.getStartY()));
            moveGantry(output_gantry, new Slot(output_gantry.getStartX(), output_gantry.getStartY()));

            for (Job outputjob : outputJobSequence) {
                Item item = outputjob.getItem();
                if (getSlot(item) == null) {
                    //Item zit nog niet in slotfield dus eerst inputs overlopen tot en met item.
                    for (int i = inputJobSequence.size(); i > 0; i--) {
                        Job inputjob = inputJobSequence.get(0);
                        if (inputjob.getItem() == item) {
                            INPUT_SLOT.setItem(inputjob.getItem());
                            inputItem(input_gantry, getClosestAvailableSlot(new Slot(input_gantry.getxMax() / 2, 0)));
                            outputItem(output_gantry, getSlot(item));
                            inputJobSequence.remove(inputjob);
                            break;
                        } else {
                            INPUT_SLOT.setItem(inputjob.getItem());
                            inputItem(input_gantry, availableSlots.get(0));
                            inputJobSequence.remove(inputjob);
                        }
                    }
                } else outputItem(output_gantry, getSlot(item));
            }

            inputJobSequence.forEach(inputjob -> {
                INPUT_SLOT.setItem(inputjob.getItem());
                inputItem(input_gantry, availableSlots.get(0));
            });


        }
    }

    public void calibrateTimes(Gantry input_gantry, Gantry output_gantry) {
        double time = Math.max(input_gantry.getTime(), output_gantry.getTime());
        input_gantry.setTime(time);
        input_gantry.setTime(time);
    }

    public boolean isCollision(Move previousPickup, Move inputplace, Move nextPickup, Move outputMove, Move outputPlace) {
        if (inputplace.getX() > outputMove.getX() - 20) {
            if (previousPickup.getTime() < outputMove.getTime() && nextPickup.getTime() > outputMove.getTime()) {
                if (outputPlace.getTime() > inputplace.getTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateGantryTimes(Gantry g, int fromIndex, double time) {
        g.getExecutedMoves().stream().forEach(move -> {
            if (g.getExecutedMoves().indexOf(move) >= fromIndex) {
                move.setTime(move.getTime() + time);
            }
        });
    }

    public Job getInputJob(Item item) {
        return inputJobSequence.stream().filter(job -> job.getItem() == item).findAny().orElse(null);
    }

    public Job getOutputJob(Item item) {
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
        gantry.addMove(gantry.moveTo(naar.getCenterX(), naar.getCenterY()));
    }

    //ELEMENTARY OPERATIONS
    //Methode om gewenst item op de pikken uit slot s
    public void pickupItemFromSlot(Gantry gantry, Slot slot) {
        if (!slot.isTopSlot()) System.out.println("ERROR SLOT NOT AVAILABLE PICKUP");
        gantry.addMove(gantry.pickupItem(slot));
        updateSlots();
    }

    //Methode om item in gantry te plaatsen in slot
    public void placeItemInSlot(Gantry gantry, Slot slot) {
        if (!slot.isAvailable() && !slot.equals(OUTPUT_SLOT)) System.out.println("ERROR SLOT NOT AVAILABLE PLACE");
        gantry.addMove(gantry.placeItem(slot));
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
        moveItem(gantry, INPUT_SLOT, s);
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

    public void createCSV(String OUTPUT_FILENAME) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_FILENAME));
        String header = "gID;T;x;y;itemInCraneID\n";

        if (gantries.size() == 1) {
            Gantry input_gantry = gantries.get(0);
            pw.write(header);

            for (Move m : input_gantry.getExecutedMoves()) {
                pw.write(m.toString());
                pw.write("\n");
            }

            pw.close();
            input_gantry.clearMoves();
        } else {

            Gantry input_gantry = gantries.get(0);
            Gantry output_gantry = gantries.get(1);

            pw.write(header);

            for (Move m : input_gantry.getExecutedMoves()) {
                pw.write(m.toString());
                pw.write("\n");
            }

            for (Move m : output_gantry.getExecutedMoves()) {
                pw.write(m.toString());
                pw.write("\n");
            }

            pw.close();
            input_gantry.clearMoves();
            output_gantry.clearMoves();
        }
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

    public void clearExecutedMoves(Gantry gantry) {
        gantry.clearMoves();
    }

    public List<Gantry> getGantries() {
        return this.gantries;
    }

}
