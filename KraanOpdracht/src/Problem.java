import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// naamgeving: availableSlots zijn slots waar er direct een item in geplaatst kan worden zonder dat het item 'valt'
public class Problem {

    private final int minX, maxX, minY, maxY;
    private final Slot INPUT_SLOT, OUTPUT_SLOT;
    private final int maxLevels;
    private final List<Item> items;
    private final List<Job> inputJobSequence;
    private final List<Job> outputJobSequence;

    private final List<Gantry> gantries;
    private final List<Slot> slots;
    private final int safetyDistance;
    private final int pickupPlaceDuration;

    List<Slot> availableSlots;
    List<Slot> occupiedSlots;
    List<Move> executedMoves;

    public Problem(int minX, int maxX, int minY, int maxY, int maxLevels, List<Item> items, List<Gantry> gantries, List<Slot> slots, List<Job> inputJobSequence, List<Job> outputJobSequence, int gantrySafetyDist, int pickupPlaceDuration) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.maxLevels = maxLevels;
        this.items = new ArrayList<>(items);
        this.gantries = new ArrayList<>(gantries);
        this.slots = new ArrayList<>(slots);
        this.inputJobSequence = new ArrayList<>(inputJobSequence);
        this.outputJobSequence = new ArrayList<>(outputJobSequence);
        this.safetyDistance = gantrySafetyDist;
        this.pickupPlaceDuration = pickupPlaceDuration;
        this.availableSlots = new ArrayList<>();
        this.occupiedSlots = new ArrayList<>();
        this.executedMoves = new ArrayList<>();
        INPUT_SLOT = slots.get(this.slots.size() - 2);
        OUTPUT_SLOT = slots.get(this.slots.size() - 1);
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Job> getInputJobSequence() {
        return inputJobSequence;
    }

    public List<Job> getOutputJobSequence() {
        return outputJobSequence;
    }

    public List<Gantry> getGantries() {
        return gantries;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public int getSafetyDistance() {
        return safetyDistance;
    }

    public int getPickupPlaceDuration() {
        return pickupPlaceDuration;
    }

    public void writeJsonFile(File file) throws IOException {
        JSONObject root = new JSONObject();

        JSONObject parameters = new JSONObject();
        root.put("parameters", parameters);

        parameters.put("gantrySafetyDistance", safetyDistance);
        parameters.put("maxLevels", maxLevels);
        parameters.put("pickupPlaceDuration", pickupPlaceDuration);

        JSONArray items = new JSONArray();
        root.put("items", items);

        for (Item item : this.items) {
            JSONObject jo = new JSONObject();
            jo.put("id", item.getId());

            items.add(jo);
        }


        JSONArray slots = new JSONArray();
        root.put("slots", slots);
        for (Slot slot : this.slots) {
            JSONObject jo = new JSONObject();
            jo.put("id", slot.getId());
            jo.put("cx", slot.getCenterX());
            jo.put("cy", slot.getCenterY());
            jo.put("minX", slot.getXMin());
            jo.put("maxX", slot.getXMax());
            jo.put("minY", slot.getYMin());
            jo.put("maxY", slot.getYMax());
            jo.put("z", slot.getZ());
            jo.put("type", slot.getType().name());
            jo.put("itemId", slot.getItem() == null ? null : slot.getItem().getId());

            slots.add(jo);
        }

        JSONArray gantries = new JSONArray();
        root.put("gantries", gantries);
        for (Gantry gantry : this.gantries) {
            JSONObject jo = new JSONObject();

            jo.put("id", gantry.getId());
            jo.put("xMin", gantry.getXMin());
            jo.put("xMax", gantry.getXMax());
            jo.put("startX", gantry.getStartX());
            jo.put("startY", gantry.getStartY());
            jo.put("xSpeed", gantry.getXSpeed());
            jo.put("ySpeed", gantry.getYSpeed());

            gantries.add(jo);
        }

        JSONArray inputSequence = new JSONArray();
        root.put("inputSequence", inputSequence);

        for (Job inputJ : this.inputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId", inputJ.getItem().getId());
            jo.put("fromId", inputJ.getPickup().getSlot().getId());

            inputSequence.add(jo);
        }

        JSONArray outputSequence = new JSONArray();
        root.put("outputSequence", outputSequence);

        for (Job outputJ : this.outputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId", outputJ.getItem().getId());
            jo.put("toId", outputJ.getPlace().getSlot().getId());

            outputSequence.add(jo);
        }

        try (FileWriter fw = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fw.write(gson.toJson(root));
        }

    }

    public List<Slot> getAvailableSlots() {
        return availableSlots;
    }

    public static Problem fromJson(File file) throws IOException, ParseException {


        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(file)) {
            JSONObject root = (JSONObject) parser.parse(reader);

            List<Item> itemList = new ArrayList<>();
            List<Slot> slotList = new ArrayList<>();
            List<Gantry> gantryList = new ArrayList<>();
            List<Job> inputJobList = new ArrayList<>();
            List<Job> outputJobList = new ArrayList<>();

            JSONObject parameters = (JSONObject) root.get("parameters");
            int safetyDist = ((Long) parameters.get("gantrySafetyDistance")).intValue();
            int maxLevels = ((Long) parameters.get("maxLevels")).intValue();
            int pickupPlaceDuration = ((Long) parameters.get("pickupPlaceDuration")).intValue();

            JSONArray items = (JSONArray) root.get("items");
            for (Object o : items) {
                int id = ((Long) ((JSONObject) o).get("id")).intValue();

                Item c = new Item(id);
                itemList.add(c);
            }


            int overallMinX = Integer.MAX_VALUE, overallMaxX = Integer.MIN_VALUE;
            int overallMinY = Integer.MAX_VALUE, overallMaxY = Integer.MIN_VALUE;

            JSONArray slots = (JSONArray) root.get("slots");
            for (Object o : slots) {
                JSONObject slot = (JSONObject) o;

                int id = ((Long) slot.get("id")).intValue();
                int cx = ((Long) slot.get("cx")).intValue();
                int cy = ((Long) slot.get("cy")).intValue();
                int minX = ((Long) slot.get("minX")).intValue();
                int minY = ((Long) slot.get("minY")).intValue();
                int maxX = ((Long) slot.get("maxX")).intValue();
                int maxY = ((Long) slot.get("maxY")).intValue();
                int z = ((Long) slot.get("z")).intValue();

                overallMinX = Math.min(overallMinX, minX);
                overallMaxX = Math.max(overallMaxX, maxX);
                overallMinY = Math.min(overallMinY, minY);
                overallMaxY = Math.max(overallMaxY, maxY);

                Slot.SlotType type = Slot.SlotType.valueOf((String) slot.get("type"));
                Integer itemId = slot.get("itemId") == null ? null : ((Long) slot.get("itemId")).intValue();
                Item c = itemId == null ? null : itemList.get(itemId);

                Slot s = new Slot(id, cx, cy, minX, maxX, minY, maxY, z, type, c);
                slotList.add(s);
            }


            JSONArray gantries = (JSONArray) root.get("gantries");
            for (Object o : gantries) {
                JSONObject gantry = (JSONObject) o;


                int id = ((Long) gantry.get("id")).intValue();
                int xMin = ((Long) gantry.get("xMin")).intValue();
                int xMax = ((Long) gantry.get("xMax")).intValue();
                int startX = ((Long) gantry.get("startX")).intValue();
                int startY = ((Long) gantry.get("startY")).intValue();
                double xSpeed = ((Double) gantry.get("xSpeed")).doubleValue();
                double ySpeed = ((Double) gantry.get("ySpeed")).doubleValue();

                Gantry g = new Gantry(id, xMin, xMax, startX, startY, xSpeed, ySpeed);
                gantryList.add(g);
            }

            JSONArray inputJobs = (JSONArray) root.get("inputSequence");
            int jid = 0;
            for (Object o : inputJobs) {
                JSONObject inputJob = (JSONObject) o;

                int iid = ((Long) inputJob.get("itemId")).intValue();
                int sid = ((Long) inputJob.get("fromId")).intValue();

                Job job = new Job(jid++, itemList.get(iid), slotList.get(sid), null);
                inputJobList.add(job);
            }

            JSONArray outputJobs = (JSONArray) root.get("outputSequence");
            for (Object o : outputJobs) {
                JSONObject outputJob = (JSONObject) o;

                int iid = ((Long) outputJob.get("itemId")).intValue();
                int sid = ((Long) outputJob.get("toId")).intValue();

                Job job = new Job(jid++, itemList.get(iid), null, slotList.get(sid));
                outputJobList.add(job);
            }

            Problem p = new Problem(overallMinX, overallMaxX, overallMinY, overallMaxY, maxLevels, itemList, gantryList, slotList, inputJobList, outputJobList, safetyDist, pickupPlaceDuration);
            p.createSlotField();
            return p;
        }

    }

    //linked-list voorstellig van slot veld
    public void createSlotField() {
        for (Slot s : slots) {
            for (Slot x : slots) {
                if (x.getZ() == s.getZ() + 1 && x.getCenterX() == s.getCenterX() && x.getCenterY() == s.getCenterY()) {
                    s.setParentSlot(x);
                }
            }
        }
        updateSlots();
    }

    //update lijst weer van available slots en occupied slots
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
        for (Slot s : occupiedSlots) {
            if (s.getItem() == item) {
                return s;
            }
        }
        return null;
    }

    public Slot getAvailableNonInterferingSlot(List<Slot> interferingSlots){
        int i =0;
        while(i<availableSlots.size()) {
            Slot available = availableSlots.get(i);
            if(!interferingSlots.contains(available)) return available;
            i++;
        }
        return null;
    }

    // move item from slot to another slot
    // bestaat uit een pickup en een place
    public void moveItem(Gantry gantry, Slot from, Slot to, Item item){
        pickUpItemFromSlot(from);
        placeItemInSlot(item,to);
        executedMoves.add(new Move(item,gantry,from,to));
    }

    public void pickUpItemFromSlot(Slot from){
        // item ophalen uit slot
        if(from.equals(INPUT_SLOT)) return;

        // slot is niet meer occupied, en wordt available
        from.putItem(null);
        occupiedSlots.remove(from);
        availableSlots.add(from);

        // er kunnen slots zijn die available waren, maar dit niet meer zijn na ophaling item
        // bij de gestapelde is dat het slot boven het slot van het op te halen item,(als het laatste geen topSlot is)
        availableSlots.removeAll(from.getRemovedAvailableSlotsPickUp());

    }


    public void placeItemInSlot(Item item, Slot to){
        if(to.equals(OUTPUT_SLOT)) return;

        // slot krijgt item, wordt occupied, en wordt niet meer available
        to.putItem(item);
        occupiedSlots.add(to);
        availableSlots.remove(to);

        // er kunnen slots zijn die available worden na het plaatsen van een item
        // bij de gestapelde is dat het slot boven het slot van het geplaatste item,(als het laatste geen topSlot is)
        availableSlots.removeAll(to.getAddedAvailableSlotsPlace());

    }


    public void processInputJob(Gantry gantry, Item item){
        // move item naar vrije plaats
        Slot availableSlot = availableSlots.get(0);
        moveItem(gantry, INPUT_SLOT, availableSlot, item);
    }

    public void processOutputJob(Gantry gantry, Item item, Slot slotVanTeVerplaatsenItem){
        // move item naar outut, eerst interfererende items verplaatsen naar niet-interfererende slots

        // slot is bereikbaar als het een topSlot is of als zijn parent available is
        if(slotVanTeVerplaatsenItem.isTopSlot() || availableSlots.contains(slotVanTeVerplaatsenItem.getParentSlot()))
            moveItem(gantry,slotVanTeVerplaatsenItem,OUTPUT_SLOT,item);

        else{
            // slot is niet available
            // interfererende slots vinden om te voorkomen dat we items verplaatsen naar slots die interfereren
            List<Slot> interferingSlots = slotVanTeVerplaatsenItem.getInterferingSlots();

            // interfering items verplaatsen
            moveInterferingItemsRecursief(slotVanTeVerplaatsenItem.getParentSlot(), interferingSlots, gantry);

            // verplaatsen van het item (dat nu bereikbaar is)
            moveItem(gantry,slotVanTeVerplaatsenItem,OUTPUT_SLOT,item);


        }
    }

    public void moveInterferingItemsRecursief(Slot s, List<Slot> interferingSlots, Gantry gantry){
        // als het slot geen topslot is en het slot heeft een parent met een item in, recursief oproepen voor die parent
        if(!s.isTopSlot() && s.hasAbove())
            moveInterferingItemsRecursief(s.getParentSlot(),interferingSlots,gantry);

        // het item in slot s moet verplaatst worden naar een available non interfering slot
        Slot available = getAvailableNonInterferingSlot(interferingSlots);
        moveItem(gantry,s,available,s.getItem());
    }


    //oplossing: eerst inputsequentie doorlopen, achteraf output behandelen + uitprinten als csv
    public void solve(String output) {

        Gantry gantry = gantries.get(0);

        for (Job j : inputJobSequence) {
            Item item = j.getItem();
            processInputJob(gantry,item);
        }

        for (Job j : outputJobSequence) {
            Item item = j.getItem();
            Slot slot = getSlot(item);
            processOutputJob(gantry,item,slot);
        }

        for(Move m: executedMoves) System.out.println(m);

    }
}
