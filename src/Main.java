import model.Country;
import model.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("input.txt"));
        PrintWriter writer = new PrintWriter(new File("output.txt"));
        int count =0;

        while (scanner.hasNextLine()){
            count ++;
            Map map = readNewMap(scanner);
            if(map.validateCoordinates()) {
                if (map.checkCountriesAccessibility()) {
                    map.prepareToStart();
                    writer.println("Test case # " + count);
                    map.doDiffusion();
                    map.showResults(writer);
                } else {
                    writer.println("Test case # " + count);
                    writer.println("Some countries are isolated!");
                }
            }
            else {
                writer.println("Test case # " + count);
                writer.println("Wrong input!");
            }
        }
        writer.close();
    }

    private static Map readNewMap(Scanner scanner){
        int countries = scanner.nextInt();
        Map map = new Map();
        for(int i=0;i<countries;i++){
            String name = scanner.next();
            int lowerLeftX = Integer.parseInt(scanner.next())-1;
            int lowerLeftY = Integer.parseInt(scanner.next())-1;
            int upperRightX = Integer.parseInt(scanner.next())-1;
            int upperRightY = Integer.parseInt(scanner.next())-1;

            Country country = new Country(name,lowerLeftX,lowerLeftY,upperRightX,upperRightY);
            map.addCountry(country);
        }
        return map;
    }


}
