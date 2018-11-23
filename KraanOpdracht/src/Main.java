import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            String INPUT_FILENAME = args[0];
            String OUTPUT_FILENAME = args[1];
            new ProblemFactory();
            Problem p = ProblemFactory.fromJson(new File(INPUT_FILENAME));
            System.out.println("Input OK.");

            p.solve();
            p.createCSV(OUTPUT_FILENAME);
            p.clearExecutedMoves();

            System.out.println("Done.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

}