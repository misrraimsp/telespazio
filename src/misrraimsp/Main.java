package misrraimsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    if (args.length != 2) {
            System.out.println("Please, this program needs as first argument a valid file path " +
                    "(relative to the program location), and as second argument a positive integer");
            return;
	    }
	    String path = args[0];
	    int maxRate = Integer.parseInt(args[1]);
        System.out.println("maxRate: " + maxRate);

	    int[] rates = new int[1440]; // 24 * 60

        try {
            FileInputStream fis = new FileInputStream(path);
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
                String[] parts = line.split(",");
                int rate = Integer.parseInt(parts[1]);
                System.out.println("Rate: " + rate);
                int startIndex = mapToIndex(parts[2]);
                System.out.println("Start Index: " + startIndex);
                int endIndex = mapToIndex(parts[3]);
                System.out.println("End Index: " + endIndex);
                aggregate(startIndex, endIndex, rate, rates);
            }
            sc.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        Arrays.stream(rates).forEach(r -> System.out.println("Rate: " + r));



    }

    private static void aggregate(int startIndex, int endIndex, int rate, int[] rates) {
        int i = startIndex;
        do {
            i++;
            rates[i%1440] += rate;
        } while (i%1440 != endIndex);
    }

    private static int mapToIndex(String dayTime) {
        String[] dayTimeParts = dayTime.split(":");
        return (60 * Integer.parseInt(dayTimeParts[0])) + Integer.parseInt(dayTimeParts[1]);
    }
}
