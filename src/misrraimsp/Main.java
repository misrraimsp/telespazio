package misrraimsp;

import java.io.*;
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
	    int maxAllowedRate = Integer.parseInt(args[1]);
        //System.out.println("maxRate: " + maxRate);


        // gather input info from file
        List<String> inputInfo = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(path);
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()) {
                inputInfo.add(sc.nextLine());
            }
            sc.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        // compute aggregate rate
        int[] rates = new int[1440]; // 24 * 60
        inputInfo.forEach(s -> {
            //System.out.println(s);
            String[] parts = s.split(",");
            int rate = Integer.parseInt(parts[1]);
            //System.out.println("Rate: " + rate);
            int startIndex = mapTimeToIndex(parts[2]);
            //System.out.println("Start Index: " + startIndex);
            int endIndex = mapTimeToIndex(parts[3]);
            //System.out.println("End Index: " + endIndex);
            aggregate(startIndex, endIndex, rate, rates);
        });
        //Arrays.stream(rates).forEach(r -> System.out.println("Rate: " + r));

        // compute max load periods
        List<Integer[]> windows = new ArrayList<>();
        int currentLoad = 0; // 1/30 of this amount is the total data transfer in a 30 minute period
        int currentMaxRate = 0;
        for (int i = 0; i < 30; i++) { // initial setup: 0 -> 29
            if (rates[i] > currentMaxRate) currentMaxRate = rates[i];
            currentLoad += rates[i];
        }
        int currentMaxLoad = currentLoad;
        windows.add(new Integer[]{0, 29});
        for (int j = 30; j < 1440; j++) { // rest: 30 -> 1439
            if (rates[j] > currentMaxRate) currentMaxRate = rates[j];
            currentLoad += (rates[j] - rates[j - 30]);
            if (currentMaxLoad < currentLoad) {
                currentMaxLoad = currentLoad;
                windows.clear();
                windows.add(new Integer[]{(j - 29), j});
            }
            else if (currentMaxLoad == currentLoad) {
                windows.add(new Integer[]{(j - 29), j});
            }
        }


        /*
        for (int k = 0; k < windows.size(); k++) {
            System.out.println("Period " + k + ": {startIndex=" + windows.get(k)[0] + "; endIndex=" + windows.get(k)[1] + "}");
        }
         */

        // translate to time values
        List<String> outputInfo = new ArrayList<>();
        windows.forEach(w -> {
            outputInfo.add(mapIndexToTime(w[0]) + "," + mapIndexToTime(w[1]));
        });

        // output max load periods to file
        String message = "{maxAllowedRate:" + maxAllowedRate + ",currentMaxRate:" + currentMaxRate + "}";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
            if (currentMaxRate > maxAllowedRate) bw.write("EXCEEDS " + message);
            else bw.write("OK " + message);
            bw.newLine();
            bw.write(String.valueOf(outputInfo.size()));
            for (String p : outputInfo) {
                bw.newLine();
                bw.write(p);
            }
            bw.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
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

    private static String mapIndexToTime(int index) {
        int hour = index / 60;
        int minute = index % 60;
        String hourString = (hour < 10) ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String minuteString = (minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute);
        return hourString + ":" + minuteString;
    }
}
