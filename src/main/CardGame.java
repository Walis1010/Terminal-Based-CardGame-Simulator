// The CardGame class is responsible for the main method and user input.
// It serves as an entry point that ensure the game is started with valid input.

import java.util.*;
import java.io.*;

public class CardGame {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int players = 0;
        String pack = "";

        GameSetup g = new GameSetup();

        // Use while loop to allow the user to re-enter input if invalid without
        // restarting the program
        while (true) {

            try {

                System.out.println("Please enter the number of players:");
                players = scanner.nextInt();

                if (players <= 0) {
                    throw new IllegalArgumentException("Number of players must be greater than 0");
                } else if (players == 1) {
                    throw new IllegalArgumentException("Number of players must be more than 1");
                }

                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Number of player must be an integer");
                scanner.nextLine();
            }

        }

        scanner.nextLine();

        // Ensure that the scanner find the pack file, assuming that the pack file is in
        // the res folder, which is in the same directory as the src folder
        System.out.println("Please enter the location of the pack to load:");
        List<Integer> cardValues = new ArrayList<>();

        while (true) {

            pack = scanner.nextLine();
            String packFilePath = "./../res/" + pack;
            File cards = new File(packFilePath);
            cardValues.clear();

            try {
                Scanner fileScanner = new Scanner(cards);
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    try {

                        cardValues.add(Integer.parseInt(line));

                    } catch (NumberFormatException e) {
                        fileScanner.close();
                        throw new IllegalArgumentException("Denomination values must be integers");
                    }

                }

                fileScanner.close();

                // Ensure that the pack contains at least 8N values so that the game can be
                // started properly
                if (cardValues.size() < players * 8) {
                    throw new IllegalArgumentException("Pack file must contain at least 8N values");

                } else {
                    cardValues = new ArrayList<>(cardValues.subList(0, players * 8));
                }

                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("Error: File not found.");
                System.out.println("Please try again:");
            }

        }

        System.out.println("\n----GAME STARTING----");
        System.out.println("---------------------");
        g.runGame(players, cardValues);
        scanner.close();

    }
}