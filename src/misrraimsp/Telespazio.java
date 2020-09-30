package misrraimsp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Telespazio {

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

    private static String mapIndexToTime(int index) {
        int hour = index / 60;
        int minute = index % 60;
        String hourString = (hour < 10) ? "0" + hour : String.valueOf(hour);
        String minuteString = (minute < 10) ? "0" + minute : String.valueOf(minute);
        return hourString + ":" + minuteString;
    }

    public static void main(String[] args) {

        /*
         * 1. Little input validation
         */
	    if (args.length != 2) {
            System.out.println("Please, this program needs as first argument a valid file path " +
                    "(relative to the program location), and as second argument a positive integer");
            return;
	    }
	    int maxAllowedRate = Integer.parseInt(args[1]);

        /*
         * 2. Gather input info from file
         */
        List<String> inputInfo = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()) {
                inputInfo.add(sc.nextLine());
            }
            sc.close();
            System.out.println("Successfully read from input file " + args[0]);
        }
        catch(FileNotFoundException e) {
            System.out.println("An IO error occurred");
            e.printStackTrace();
        }

        /*
         * 3. Compute all satellites aggregate rate
         */
        if (inputInfo.isEmpty()) {
            System.out.println("No satellite pass info inside the input file");
            return;
        }
        int[] rates = new int[1440]; // 24 * 60
        inputInfo.forEach(s -> {
            String[] parts = s.split(",");
            int rate = Integer.parseInt(parts[1]);
            int startIndex = mapTimeToIndex(parts[2]);
            int endIndex = mapTimeToIndex(parts[3]);
            aggregate(startIndex, endIndex, rate, rates);
        });

        /*
         * 4. Compute max load period/s
         */
        List<Integer[]> maxLoadWindows = new ArrayList<>();
        int currentLoad = 0; // this var will hold the 1/30 of the total data transfer in a 30 minute period
        int currentMaxRate = 0;
        for (int i = 0; i < 30; i++) { // first 30min window (indexes 0 -> 29)
            // keeping track max rate
            if (rates[i] > currentMaxRate) currentMaxRate = rates[i];
            // compute this window load
            currentLoad += rates[i];
        }
        int currentMaxLoad = currentLoad;
        maxLoadWindows.add(new Integer[]{0, 29});
        for (int j = 30; j < 1440; j++) { // remaining 30min windows (indexes 30 -> 1439)
            // keeping track max rate
            if (rates[j] > currentMaxRate) currentMaxRate = rates[j];
            // compute this window load
            currentLoad += (rates[j] - rates[j - 30]);
            // keeping track max load
            if (currentMaxLoad < currentLoad) {
                currentMaxLoad = currentLoad;
                maxLoadWindows.clear();
                maxLoadWindows.add(new Integer[]{(j - 29), j});
            }
            else if (currentMaxLoad == currentLoad) {
                maxLoadWindows.add(new Integer[]{(j - 29), j});
            }
        }

        /*
         * 5. Translate indexes info to time values
         */
        List<String> outputInfo = new ArrayList<>();
        maxLoadWindows.forEach(w -> outputInfo.add(mapIndexToTime(w[0]) + "," + mapIndexToTime(w[1])));

        /*
         * 6. Output max load time periods to file
         */
        String message = "{maxAllowedRate:" + maxAllowedRate + ",currentMaxRate:" + currentMaxRate + "}";
        String outputFileName = "out.txt";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
            if (currentMaxRate > maxAllowedRate) bw.write("EXCEEDS " + message);
            else bw.write("OK " + message);
            bw.newLine();
            bw.write(String.valueOf(outputInfo.size()));
            for (String p : outputInfo) {
                bw.newLine();
                bw.write(p);
            }
            bw.close();
            System.out.println("Successfully wrote to " + outputFileName);
        } catch (IOException e) {
            System.out.println("An IO error occurred");
            e.printStackTrace();
        }
    }
}
