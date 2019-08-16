import java.util.Scanner;

public class RockPaperScissors {
    public static void main(String[] args) {

        // Player#Count is used to display score
        int PlayerOneCount = 0, PlayerTwoCount = 0;
        Scanner in = new Scanner(System.in);
        int PlayerOne = 0;
        int PlayerTwo = 0;

        /*
         * while loop loop the whole game to start in the beginning and if playerOne didn't option 4
         */

        while (PlayerOne != 4 || PlayerTwo != 4) {
            System.out.println("Player One: Choose your weapon!");
            PlayerOne = in.nextInt();
            /*
             * if playerOne chooses option 4 the game will quit
             */
            if(PlayerOne == 4 || PlayerTwo == 4){

                if (PlayerOneCount == PlayerTwoCount){
                    System.out.println("Player One and Player Two are tied with " + PlayerOneCount  + " points.");

                }
                if (PlayerOneCount > PlayerTwoCount){
                    System.out.println("The winner is Player One with " + PlayerOneCount + " points.");

                }
                if (PlayerOneCount < PlayerTwoCount){
                    System.out.println("The winner is Player Two with " + PlayerTwoCount + " points.");
                }

                System.out.println("Quitting");
                break;
            }
            System.out.println("Good choice!");
            System.out.println("Player Two: Choose your weapon!");
            PlayerTwo = in.nextInt();
            System.out.println("Good choice!");

            /*
             * here player have to choose their weapons
             */
            if (PlayerOne == 1) {
                System.out.println("Player One chose: rock");
            } else if (PlayerOne == 2) {
                System.out.println("Player One chose: paper");
            } else if (PlayerOne == 3) {
                System.out.println("Player One chose: scissors");
            }

            /*
             * here player have to choose their weapons
             */

            if (PlayerTwo == 1) {
                System.out.println("Player Two chose: rock");
            } else if (PlayerTwo == 2) {
                System.out.println("Player Two chose: paper");
            } else if (PlayerTwo == 3) {
                System.out.println("Player Two chose: scissors");
            }

            /*
             * if both players chose the same weapon it a tie (draw) and the score will display
             */
            if (PlayerOne == PlayerTwo) {
                System.out.println("It's a draw!");
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);

                /*
                 * if playerTwo choose option 2 and playerOne choose 1, playerTwo wins and the score will display
                 */
            } else if (PlayerOne == 1 && PlayerTwo == 2) {
                System.out.println("Player Two Wins!");
                PlayerTwoCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }

            /*
             * if playerOne choose option 1 and playerTwo choose 1, playerOne wins and the score will display
             */
            else if (PlayerOne == 1 && PlayerTwo == 3) {
                System.out.println("Player One Wins!");
                PlayerOneCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }

            /*
             * if playerOne choose option 2 and playerTwo choose 1, playerOne wins and the score will display
             */
            else if (PlayerOne == 2 && PlayerTwo == 1) {
                System.out.println("Player One Wins!");
                PlayerOneCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }

            /*
             * if playerOne choose option 2 and playerTwo choose 3, playerTwo wins and the score will display
             */
            else if (PlayerOne == 2 && PlayerTwo == 3) {
                System.out.println("Player Two Wins!");
                PlayerTwoCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }

            /*
             * if playerOne choose option 3 and playerTwo choose 1, playerTwo wins and the score will display
             */
            else if (PlayerOne == 3 && PlayerTwo == 1) {
                System.out.println("Player Two Wins!");
                PlayerTwoCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }

            /*
             * if playerOne choose option 3 and playerTwo choose 2, playerTwo wins and the score will display
             */
            else if (PlayerOne == 3 && PlayerTwo == 2) {
                System.out.println("Player One Wins!");
                PlayerOneCount += 1;
                System.out.println("The score is now: " + PlayerOneCount + " - " + PlayerTwoCount);
            }
        }
    }
}
