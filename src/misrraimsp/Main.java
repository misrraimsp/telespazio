package misrraimsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        try {
            FileInputStream fis = new FileInputStream(path);
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
            sc.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("maxRate: " + maxRate);
    }
}
