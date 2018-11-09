import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static final String INPUT_FILENAME = "1_10_100_4_FALSE_65_50_50.json";
    public static final String OUTPUT_FILENAME= "Output_Kraanopdracht_Groep4.csv";

    public static void main(String[] args) {

        try {

            Problem p=Problem.fromJson(new File(INPUT_FILENAME));

            p.solve(OUTPUT_FILENAME);

            System.out.println("Input OK.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

}