package misrraimsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                int startIndex = mapTimeToIndex(parts[2]);
                System.out.println("Start Index: " + startIndex);
                int endIndex = mapTimeToIndex(parts[3]);
                System.out.println("End Index: " + endIndex);
                aggregate(startIndex, endIndex, rate, rates);
            }
            sc.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        Arrays.stream(rates).forEach(r -> System.out.println("Rate: " + r));

        // obtain the max load periods
        //
        //
        List<Integer[]> indexes = new ArrayList<>();

        //initial setup: 0 -> 29
        int currentLoad = 0;
        for (int i = 0; i < 30; i++) {
            currentLoad += rates[i];
        }
        int max = currentLoad;
        indexes.add(new Integer[]{0, 29});
        //rest: 30 -> 1439
        for (int j = 30; j < 1440; j++) {
            currentLoad += (rates[j] - rates[j - 30]);
            if (max < currentLoad) {
                max = currentLoad;
                indexes.clear();
                indexes.add(new Integer[]{(j - 29), j});
            }
            else if (max == currentLoad) {
                indexes.add(new Integer[]{(j - 29), j});
            }
        }


        for (int k = 0; k < indexes.size(); k++) {
            System.out.println("Period " + k + ": {startIndex=" + indexes.get(k)[0] + "; endIndex=" + indexes.get(k)[1] + "}");
        }

    }

    private static void aggregate(int startIndex, int endIndex, int rate, int[] rates) {
        int i = startIndex;
        do {
            i++;
            rates[i%1440] += rate;
        } while (i%1440 != endIndex);
    }

    private static int mapTimeToIndex(String dayTime) {
        String[] dayTimeParts = dayTime.split(":");
        return (60 * Integer.parseInt(dayTimeParts[0])) + Integer.parseInt(dayTimeParts[1]);
    }
}
