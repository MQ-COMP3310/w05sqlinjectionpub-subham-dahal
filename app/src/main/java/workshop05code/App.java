package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Connected to the database.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            logger.log(Level.SEVERE, "Not able to connect to the database.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "Created the tables.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            logger.log(Level.SEVERE, "Not able to create the tables.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if(line.matches("^[a-z]{4}$")){  
                wordleDatabaseConnection.addValidWord(i, line);
                logger.log(Level.INFO, "Added word "+line+" to the database.");
                }else{
                    logger.log(Level.SEVERE, "Line "+i+" is not a 4 letter word: "+line);
                }
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Not able to load data.txt file.");
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if(guess.matches("^[a-z]{4}$")){
                    logger.log(Level.INFO, "User entered a word: "+guess);
                System.out.println("You've guessed '" + guess+"'.");

                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    logger.log(Level.INFO, "User guess was correct: "+guess);
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    logger.log(Level.INFO, "User guess was incorrect: "+guess);
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }
            }else{
                System.out.println("Sorry. This word "+guess+" is NOT a 4 letter word.\n");
                logger.log(Level.INFO, "User entered a word that is not 4 letters: "+guess);
            }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            
        }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Not able to read from the console.");
            e.printStackTrace();
        }

    }
}