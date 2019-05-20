package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class FileHelper {
    public static void main(String[] args) {
        try {
            File file = new File("input.txt");
            if (file.createNewFile()) {
                System.out.println("File " + file.getName() + " just created. Please try again.");
            } else {
                //System.out.println("file already exists");
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while ( line != null ) {
                    sb.append( line );
                    sb.append( '\n' );
                    line = br.readLine();
                }


                br.close();
            }
        } catch (IOException e) {
            System.err.println("error occurred!");
        }
    }
}
