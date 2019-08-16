import java.util.Scanner;

public class EatCupcake {

    public static void main(String[] args) {


        //In this project I used neseted if statements (if statement inside an if statment)

        Scanner input = new Scanner(System.in);

        System.out.println("Did anyone see you?");
        String testEatCupcake = input.nextLine();

        if (testEatCupcake.equals("YES.")) {
            System.out.println("Was it a boss/lover/parent?");
            testEatCupcake = input.nextLine();
            if (testEatCupcake.equals("YES.")) {
                System.out.println("Was it expensive?");
                testEatCupcake = input.nextLine();
                if (testEatCupcake.equals("YES.")) {
                    System.out.println("Can you cut off the part that touched the floor?");
                    testEatCupcake = input.nextLine();
                    if (testEatCupcake.equals("YES.")) {
                        System.out.println("EAT IT.");
                    } else {
                        System.out.println("YOUR CALL");
                    }
                } else if (testEatCupcake.equals("NO.")) {
                    System.out.println("Is it chocolate?");
                    testEatCupcake = input.nextLine();
                    if (testEatCupcake.equals("YES.")) {
                        System.out.println("EAT IT.");
                    } else {
                        System.out.println("DON'T EAT IT");
                    }
                }
            } else {
                System.out.println("EAT IT.");
            }

        } else if (testEatCupcake.equals("NO.")) {
            System.out.println("Was it sticky?");
            testEatCupcake = input.nextLine();
            if (testEatCupcake.equals("YES.")) {
                System.out.println("Is it a raw steak?");
                testEatCupcake = input.nextLine();
                if (testEatCupcake.equals("YES.")) {
                    System.out.println("Are you a puma?");
                    if (testEatCupcake.equals("YES.")) {
                        System.out.println("EAT IT.");
                    } else {
                        System.out.println("DON'T EAT IT");
                    }
                } else if (testEatCupcake.equals("NO.")) {
                    System.out.println("Did the cat lick it?");
                    testEatCupcake = input.nextLine();
                    if (testEatCupcake.equals("YES.")) {
                        System.out.println("Is your cat healthy?");
                        testEatCupcake = input.nextLine();
                        if (testEatCupcake.equals("YES.")) {
                            System.out.println("EAT IT.");
                        } else {
                            System.out.println("YOUR CALL");
                        }
                    } else {
                        System.out.println("EAT IT.");
                    }
                }
            } else if (testEatCupcake.equals("NO.")) {
                System.out.println("Is it an Emausaurus?");
                testEatCupcake = input.nextLine();
                if (testEatCupcake.equals("YES.")) {
                    System.out.println("Are you a Megalosaurus?");
                    testEatCupcake = input.nextLine();
                    if (testEatCupcake.equals("YES.")) {
                        System.out.println("EAT IT.");
                    } else {
                        System.out.println("DON'T EAT IT");
                    }
                } else if (testEatCupcake.equals("NO.")) {
                    System.out.println("Did the cat lick it?");
                    testEatCupcake = input.nextLine();
                    if (testEatCupcake.equals("NO.")) {
                        System.out.println("EAT IT.");
                    } else if (testEatCupcake.equals("YES.")) {
                        System.out.println("Is your cat healthy?");
                        testEatCupcake = input.nextLine();
                        if (testEatCupcake.equals("YES.")) {
                            System.out.println("EAT IT.");
                        } else {
                            System.out.println("YOUR CALL");
                        }
                    }
                }
            }
        }
    }
}










