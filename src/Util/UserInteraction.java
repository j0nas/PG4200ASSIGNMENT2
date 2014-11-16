package Util;

import java.util.Scanner;

/**
 * Convenience class containing everything related to interaction with the user.
 */
public class UserInteraction {
    /**
     * Queries the user whether s/he would like to stop querying. Continues to prompt until valid (y/n) answer is provided.
     *
     * @param scanner The scanner to use to receive user input.
     * @return true if user wishes to exit, false otherwise.
     */
    public static boolean userWishesToExit(final Scanner scanner) {
        System.out.print("Do you wish to stop querying? (y/n) ");
        String userResponse;
        while (!(userResponse = scanner.nextLine()).equalsIgnoreCase("y") && !(userResponse.equalsIgnoreCase("n"))) {
            System.out.println("Please enter either 'y' or 'n'.");
        }

        return userResponse.equalsIgnoreCase("y");
    }


}
