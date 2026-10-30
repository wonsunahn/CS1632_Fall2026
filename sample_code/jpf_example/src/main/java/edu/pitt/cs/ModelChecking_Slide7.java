package edu.pitt.cs;

import java.util.Random;

public class ModelChecking_Slide7 {
    public static void main(String[] args) {
        Random random = new Random(); // (1)

        int x = 0;
        while(true) {
            x = random.nextInt(2);
        }

    }
}
