package edu.pitt.cs;

import java.util.Random;

public class ModelChecking_Slide5 {
    public static void main(String[] args) {
        Random random = new Random(); // (1)

        while(true) {
            int x = random.nextInt(2);
            if(x == 1) break;
        }

    }
}
