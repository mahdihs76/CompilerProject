package utility;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class FileHelper {

    public static String readInput(String fileName){
        ////////////////////////////////////////////////////////////////
        ////////////////*** reading from input file ***/////////////////
        ////////////////////////////////////////////////////////////////
        StringBuilder inputSB = new StringBuilder("");

        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.err.println("File " + file.getName() + " just created. Please try again.");
            } else {
                //System.out.println("file already exists");
                BufferedReader br = new BufferedReader(new FileReader(file));
                inputSB = new StringBuilder();
                String line = br.readLine();

                while ( line != null ) {
                    inputSB.append( line );
                    inputSB.append( '\n' );
                    line = br.readLine();
                }

                br.close();
            }
        } catch (IOException e) {
            System.err.println("Error occurred in reading!");
        }

        return inputSB.toString();

    }

    public static void writeOutput(String outputString, String fileName){
        ////////////////////////////////////////////////////////////////
        ////////////////*** reading from input file ***/////////////////
        ////////////////////////////////////////////////////////////////

        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.err.println("File " + file.getName() + " just created. Please try again.");
            } else {
                //System.out.println("file already exists");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(outputString);

                bw.close();
            }
        } catch (IOException e) {
            System.err.println("Error occurred in writing!");
        }

    }

}
