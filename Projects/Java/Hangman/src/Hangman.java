import java.util.Scanner;

public class Hangman {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] words = new String[1];
        String word = "";
        int count = 0;
        System.out.println("Player One, enter a word:");
        String player1_word = sc.next();

        /*
        Here Player One will enter a word that Player Two will have to guess.
         */
        for (int i = 0; i < words.length; i++) {
            words[i] = player1_word;
            word = words[i];
        }

        /*
        The word that is entered by Player One will be replaced by dash(_) excluding spaces
         */
        String phura = new String(new char[word.length()]).replace("\0", "_");
        System.out.println(phura);

        /*
        These are guesses (life)
         */
        int life = 8;

        /*
        Here is where the game starts and Player Two will have to guess the word.
         */
        while (count < 8 && phura.contains("_")) {
            System.out.println("Player Two, you have " + life + " guesses left. Enter a guess:");
            String guess = sc.next();
            String new_underscore = "";
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess.charAt(0)) {
                    new_underscore += guess.charAt(0);
                } else if (phura.charAt(i) != '_') {
                    new_underscore += word.charAt(i);
                } else {
                    new_underscore += "_";
                }
            }

            /*
            If Player Two guessed the letter that is already guessed, the computer will tell Player Two and guesses will
            not decrease
             */
            if(phura.contains(String.valueOf(guess))) {
                System.out.println("You have already guessed that letter.");
            }
            else if (phura.equals(new_underscore)) {
                life--;
                count++;
                if(count < 8){
                    System.out.println(phura);
                }
            } else {
                phura = new_underscore;
                System.out.println(phura);
            }

            /*
            If Player Two guessed the whole word, Player Two wins1
             */
            if (phura.equals(word)) {
                System.out.println("Game over. Player Two wins!");
            }
            /*
            But if Player Two didn't guess the whole word and its life runs out, Player One wins and the word will be revealed.
             */
            else if(count == 8){
                System.out.println("Game over. Player One wins! The word was: " + word);
            }
        }
        sc.close();
    }
}
