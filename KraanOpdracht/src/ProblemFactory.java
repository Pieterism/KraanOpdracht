import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProblemFactory {
    int minX, maxX, minY, maxY;
    int maxLevels;
    List<Item> items;
    List<Job> inputJobSequence;
    List<Job> outputJobSequence;
    List<Gantry> gantries;
    List<Slot> slots;
    int safetyDistance;
    int pickupPlaceDuration;

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

            Problem p = new Problem(inputJobList, outputJobList, gantryList, slotList);
            p.createSlotField();
            return p;
        }

    }
}
