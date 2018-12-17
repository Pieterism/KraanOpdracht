package main;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String INPUT_FILENAME = "1_10_100_4_FALSE_65_50_50.json";
        String OUTPUT_FILENAME = "Output_Kraanopdracht_Groep4.csv";
        try {

            //String INPUT_FILENAME = args[0];
            //String OUTPUT_FILENAME = args[1];

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