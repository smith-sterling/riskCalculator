package testing;

import utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static utils.Pair.p;

public class Take3 {
    private static int numWays(int numDice) { return (int) Math.pow(6, numDice); }

    //region One Casualty
    private static final double MAGIC_1_1_TO_1_0 = 15./numWays(2);
    private static final double MAGIC_1_1_TO_0_1 = 1 - MAGIC_1_1_TO_1_0;

    private static final double MAGIC_1_2_TO_0_2 = 161./numWays(3);
    private static final double MAGIC_1_2_TO_1_1 = 1 - MAGIC_1_2_TO_0_2;

    private static final double MAGIC_2_1_TO_2_0 = 125./numWays(3);
    private static final double MAGIC_2_1_TO_1_1 = 1 - MAGIC_2_1_TO_2_0;

    private static final double MAGIC_3_1_TO_3_0 = 855./numWays(4);
    private static final double MAGIC_3_1_TO_2_1 = 1 - MAGIC_3_1_TO_3_0;
    //endregion One Casualty

    //region Two Casualties
    private static final double MAGIC_2_2_TO_2_0 = 295./numWays(4);
    private static final double MAGIC_2_2_TO_0_2 = 581./numWays(4);
    private static final double MAGIC_2_2_TO_1_1 = 1 - MAGIC_2_2_TO_2_0 - MAGIC_2_2_TO_0_2;

    private static final double MAGIC_3_2_TO_3_0 = 2890./numWays(5);
    private static final double MAGIC_3_2_TO_2_1 = 2611./numWays(5);
    private static final double MAGIC_3_2_TO_1_2 = 1 - MAGIC_3_2_TO_3_0 - MAGIC_3_2_TO_2_1;
    //endregion Two Casualties

    //region Final outcomes
    private static final double MAGIC_FINAL_1_2_TO_0_1 = MAGIC_1_2_TO_1_1 * MAGIC_1_1_TO_0_1;
    private static final double MAGIC_FINAL_1_2_TO_1_0 = MAGIC_1_2_TO_1_1 * MAGIC_1_1_TO_1_0;

    private static final double MAGIC_FINAL_2_1_TO_1_0 = MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_1_0;
    private static final double MAGIC_FINAL_2_1_TO_0_1 = MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_0_1;

    private static final double MAGIC_FINAL_3_1_TO_2_0 = MAGIC_3_1_TO_2_1 * MAGIC_2_1_TO_2_0;
    private static final double MAGIC_FINAL_3_1_TO_1_0 = MAGIC_3_1_TO_2_1 * MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_1_0;
    private static final double MAGIC_FINAL_3_1_TO_0_1 = MAGIC_3_1_TO_2_1 * MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_0_1;

    private static final double MAGIC_FINAL_3_2_TO_2_0 = MAGIC_3_2_TO_2_1 * MAGIC_2_1_TO_2_0;
    private static final double MAGIC_FINAL_3_2_TO_1_0 = MAGIC_3_2_TO_1_2 * MAGIC_1_2_TO_1_1 * MAGIC_1_1_TO_1_0 +
                                       MAGIC_3_2_TO_2_1 * MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_1_0;
    private static final double MAGIC_FINAL_3_2_TO_0_1 = MAGIC_3_2_TO_1_2 * MAGIC_1_2_TO_1_1 * MAGIC_1_1_TO_0_1 +
                                       MAGIC_3_2_TO_2_1 * MAGIC_2_1_TO_1_1 * MAGIC_1_1_TO_0_1;
    private static final double MAGIC_FINAL_3_2_TO_0_2 = MAGIC_3_2_TO_1_2 * MAGIC_1_2_TO_0_2;
    //endregion Final Outcomes

    private static final List<Pair> TWO_CASUALTY_OPTIONS = List.of(p(2, 0), p(1, 1), p(0, 2));
    private static final List<Pair> ONE_CASUALTY_OPTIONS = List.of(p(1, 0), p(0, 1));
    
    private static final Map<Pair, Map<Pair, Double>> battleChances = new TreeMap<>();
//    private static final Map<Pair, Map<Pair, Long>> battleOptions = new TreeMap<>();


//    private static Pair p(int f, int s) { return new Pair(f, s); }

    static {
        generateBaseCases();
    }

    public static void main(String[] args) {
        getChances(5, 2);
        printAllOutcomes();
//        printOutcomes(battleOutcomes);
    }

    private static void printAllOutcomes() {
        battleChances.keySet().stream().toList().forEach(before -> {
            for (int i = 1; i <= before.first; ++i) {
                getChances(i, before.second);
            }
            for (int i = 1; i <= before.second; i++) {
                getChances(before.first, i);
            }
        });
        for (var before : battleChances.entrySet()) {
            if (before.getKey().first <= 0 || before.getKey().second <= 0) continue;

            System.out.printf("""
                    
                    %d vs %d
                    """, before.getKey().first, before.getKey().second);
            for (var after : before.getValue().entrySet()) {
                System.out.printf("""
                            %.2f%% - %d vs %d
                        """,
                        after.getValue() * 100,
                        after.getKey().first,
                        after.getKey().second
                );
            }
        }
    }
    private static void printOutcomes(Map<Pair, Map<Pair, Double>> map) {
        for (var before : map.entrySet()) {
            if (before.getKey().first <= 0 || before.getKey().second <= 0) continue;
            System.out.printf("""
                    %d Attacking %d
                    """, before.getKey().first, before.getKey().second);
            for (var after : before.getValue().entrySet()) {
                System.out.printf("""
                            Remaining Attacking: %d
                            Remaining Defending: %d
                            Percent Chance:       %.2f%%
                            
                        """,
                        after.getKey().first,
                        after.getKey().second,
                        after.getValue() * 100
                );
            }
        }
    }

    private static Map<Pair, Double> getChances(int attackingTroops, int defendingTroops) {
        Pair before = p(attackingTroops, defendingTroops);
        if (battleChances.containsKey(before)){
            return battleChances.get(before);
        }
        return generateChances(attackingTroops, defendingTroops);
    }

    private static Map<Pair, Double> generateChances(int attackingTroops, int defendingTroops) {
        Map<Pair, Double> ret = new TreeMap<>();
        for (int i = attackingTroops; i >= 1; i--) {
            ret.put(p(i, defendingTroops), getChance(attackingTroops, defendingTroops, i, defendingTroops));
//            ret.put(p(i, defendingTroops), 0.);
        }
        for (int i = defendingTroops; i >= 1; i--) {
            ret.put(p(attackingTroops, i), getChance(attackingTroops, defendingTroops, attackingTroops, i));
//            ret.put(p(attackingTroops, i), 0.);
        }
//
//        int casualties = Math.min(Math.min(attackingTroops, defendingTroops), 2);
//        int attackDice = Math.min(3, attackingTroops);
//        int defenceDice = Math.min(2, attackingTroops);
//


//        if (casualties == 2) {
            //sum of the chances to get to each
//            double killTwoAttackers = getChance(attackingTroops, defendingTroops, attackingTroops - 2, defendingTroops);
//            double killTwoDefenders = getChance(attackingTroops, defendingTroops, attackingTroops, defendingTroops - 2);
//            double killOneOfEach = getChance(attackingTroops, defendingTroops, attackingTroops - 1, defendingTroops - 1);

//            Map<Pair, Double> killTwoAttackers = getChances(attackingTroops - 2, defendingTroops);
//            Map<Pair, Double> killTwoDefenders = getChances(attackingTroops, defendingTroops - 2);
//            Map<Pair, Double> killOneOfEach = getChances(attackingTroops - 1, defendingTroops - 1);
//        } else if (casualties == 1) {
//            Map<Pair, Double> killAnAttacker = getChances(attackingTroops - 1, defendingTroops);
//            for (var entry : killAnAttacker.entrySet()) {
//
//            }
//            Map<Pair, Double> killADefender = getChances(attackingTroops, defendingTroops - 1);
//            for (var entry : killADefender.entrySet()) {
//
//            }
//        } else {
//            throw new RuntimeException("Something's not right...");
//        }

        battleChances.put(p(attackingTroops, defendingTroops), ret);
        return ret;
    }

    private static Map<Pair, Double> getMagicChances(int attackDice, int defenceDice) {
        if (attackDice == 1) {
            if (defenceDice == 1) {
                return Map.of(p(1, 0), MAGIC_1_1_TO_1_0,
                              p(0, 1), MAGIC_1_1_TO_0_1
                );
            } else if (defenceDice == 2) {
                return Map.of(p(0, 2), MAGIC_1_2_TO_0_2,
                              p(0, 1), MAGIC_FINAL_1_2_TO_0_1,
                              p(1, 0), MAGIC_FINAL_1_2_TO_1_0
                );
            }
        } else if (attackDice == 2) {
            if (defenceDice == 1) {
                return Map.of(p(2, 0), MAGIC_2_1_TO_2_0,
                              p(1, 0), MAGIC_FINAL_2_1_TO_1_0,
                              p(0, 1), MAGIC_FINAL_2_1_TO_0_1
                );
            } else if (defenceDice == 2) {
                return Map.of(p(2, 0), MAGIC_2_2_TO_2_0,
                              p(1, 1), MAGIC_2_2_TO_1_1,
                              p(0, 2), MAGIC_2_2_TO_0_2
                );
            }
        } else if (attackDice == 3) {
            if (defenceDice == 1) {
                return Map.of(p(3, 0), MAGIC_3_1_TO_3_0,
                              p(2, 0), MAGIC_FINAL_3_1_TO_2_0,
                              p(1, 0), MAGIC_FINAL_3_1_TO_1_0,
                              p(0, 1), MAGIC_FINAL_3_1_TO_0_1
                );
            } else if (defenceDice == 2) {
                return Map.of(p(3, 0), MAGIC_3_2_TO_3_0,
                              p(2, 0), MAGIC_FINAL_3_2_TO_2_0,
                              p(1, 0), MAGIC_FINAL_3_2_TO_1_0,
                              p(0, 1), MAGIC_FINAL_3_2_TO_0_1,
                              p(0, 2), MAGIC_FINAL_3_2_TO_0_2
                );
            }
        }
        throw new RuntimeException("Bad bad");
    }

    private static double getChance(int attackingTroops, int defendingTroops,
                                   int remainingAttackingTroops, int remainingDefendingTroops) {
        Pair before = p(attackingTroops, defendingTroops);
        Pair after = p(remainingAttackingTroops, remainingDefendingTroops);

        if (attackingTroops == remainingAttackingTroops && defendingTroops == remainingDefendingTroops) {
            return 0.;
        } else if (battleChances.containsKey(before) && battleChances.get(before).containsKey(after)) {
            return battleChances.get(before).get(after);
        } else if (getCasualties(attackingTroops, defendingTroops) >
                   getRequestedCasualties(attackingTroops, defendingTroops, remainingAttackingTroops, remainingDefendingTroops)) {
            return 0.;
//            generateChances(remainingAttackingTroops, remainingDefendingTroops);
//            return battleOutcomes.get(before).get(after);
//        } else if (getCasualties(attackingTroops, defendingTroops) ==
//                   getRequestedCasualties(attackingTroops, defendingTroops, remainingAttackingTroops, remainingDefendingTroops)) {
//            generateChances(remainingAttackingTroops, remainingDefendingTroops);
//            return battleOutcomes.get(before).get(after);
        }


        int casualties = getCasualties(attackingTroops, defendingTroops);
        if (casualties != 1 && casualties != 2) {
            generateChances(attackingTroops, defendingTroops);
            return battleChances.get(before).get(after);
        }

        int expectedAttackCasualties = attackingTroops - remainingAttackingTroops;
        int expectedDefenseCasualties = defendingTroops - remainingDefendingTroops;

        if (casualties == 1) {
            if (defendingTroops >= 2) {
                //attacking with 1
                return expectedAttackCasualties == 1
                        ? MAGIC_1_2_TO_0_2 : MAGIC_1_2_TO_1_1;
            } else if (attackingTroops >= 3) {
                return expectedAttackCasualties == 1
                        ? MAGIC_3_1_TO_2_1 : MAGIC_3_1_TO_3_0;
            } else if (attackingTroops == 2) {
                return expectedAttackCasualties == 1
                        ? MAGIC_2_1_TO_1_1 : MAGIC_2_1_TO_2_0;
            } else if (attackingTroops == 1) {
                return expectedAttackCasualties == 1
                        ? MAGIC_1_1_TO_0_1 : MAGIC_1_1_TO_1_0;
            }
            throw new RuntimeException("Something isn't right here");
        } else {
            //attacking with either 2 or 3 and defending with 2
            if (attackingTroops >= 3) {
                return expectedAttackCasualties == 2 ? MAGIC_3_2_TO_1_2
                        : expectedAttackCasualties == 1 ? MAGIC_3_2_TO_2_1
                        : MAGIC_3_2_TO_3_0;
            } else if (attackingTroops == 2) {
                //2 vs 2
                return expectedAttackCasualties == 2 ? MAGIC_2_2_TO_0_2
                        : expectedAttackCasualties == 1 ? MAGIC_2_2_TO_1_1
                        : MAGIC_2_2_TO_2_0;
            }
            throw new RuntimeException("Something is not right here...");
        }
    }

    private static int getRequestedCasualties(int attackingTroops, int defendingTroops, int remainingAttackingTroops,
                                              int remainingDefendingTroops) {
        return (attackingTroops - remainingAttackingTroops) + (defendingTroops - remainingDefendingTroops);
    }

    private static int getCasualties(int attackingTroops, int defendingTroops) {
        return Math.min(Math.min(attackingTroops, defendingTroops), 2);
    }


    private static void generateBaseCases() {
        for (int i = 1; i <= 3; ++i) {
            for (int j = 1; j <= 2; j++) {
                battleChances.computeIfAbsent(p(i, j), k -> new TreeMap<>())
                        .putAll(getMagicChances(i, j));
            }
        }
    }

}

