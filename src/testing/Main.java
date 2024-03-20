package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

    }

    private static void other() {
        List<List<Integer>> attacking = new ArrayList<>();
        List<List<Integer>> defending = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                for (int k = 1; k < 7; k++) {
                    List<Integer> list = new ArrayList<>(List.of(i, j, k));
                    list.remove(Collections.min(list));
                    attacking.add(list);
                }
            }
        }
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                defending.add(new ArrayList<>(List.of(i, j)));
            }
        }

        System.out.println("Attack with 3:");
        printChances(attacking, defending);
        System.out.println("\nAttack with 2:");
        attacking = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                attacking.add(new ArrayList<>(List.of(i, j)));
            }
        }
        printChances(attacking, defending);
    }

    private static void printChances(List<List<Integer>> attacking, List<List<Integer>> defending) {
        int attackingWins = 0, defendingWins = 0, oneAndOne = 0;

        for (var attack : attacking) {
            for (var defend : defending) {
                int maxAttack = Collections.max(attack), minAttack = Collections.min(attack);
                int maxDefend = Collections.max(defend), minDefend = Collections.min(defend);
                int attackScore = 0;
                if (maxDefend >= maxAttack) attackScore--;
                else attackScore++;
                if (minDefend >= minAttack) attackScore--;
                else attackScore++;
                switch (attackScore) {
                    case -2 -> defendingWins++;
                    case 0 -> oneAndOne++;
                    case 2 -> attackingWins++;
                }
            }
        }
        double chanceA = (double) attackingWins / (attackingWins + oneAndOne + defendingWins);
        double chanceB = (double) oneAndOne / (attackingWins + oneAndOne + defendingWins);
        double chanceD = (double) defendingWins / (attackingWins + oneAndOne + defendingWins);
        System.out.printf("""
            Attacking wins both: %d (%f%%)
            Both lose one:       %d (%f%%)
            Defending wins both: %d (%f%%)
            %n""", attackingWins, chanceA, oneAndOne, chanceB, defendingWins, chanceD);
    }
}


