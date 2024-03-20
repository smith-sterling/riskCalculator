package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class BetterChances {
    //attacking to defending to list of all chances of casualties
    private static final Map<Long, Map<Long, Set<BattleOutcome>>> battleOutcomes;
    private static final Map<Long, Map<Long, BattleOutcome>> singleBattle;

    private static class BattleOutcome implements Comparable<BattleOutcome> {
        long attackingCasualties;
        long defendingCasualties;
        double percentChance;


        public BattleOutcome(long attackingCasualties, long defendingCasualties, double percentChance) {
            this.attackingCasualties = attackingCasualties;
            this.defendingCasualties = defendingCasualties;
            this.percentChance = percentChance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BattleOutcome battleOutcome = (BattleOutcome) o;
            return attackingCasualties == battleOutcome.attackingCasualties
                   && defendingCasualties == battleOutcome.defendingCasualties;
        }

        @Override
        public int hashCode() {
            return Objects.hash(attackingCasualties, defendingCasualties);
        }

        @Override
        public String toString() {
            return "Stats{" +
                   "attackingCasualties=" + attackingCasualties +
                   ", defendingCasualties=" + defendingCasualties +
                   ", numberOfWays=" + percentChance +
                   '}';
        }
        @Override
        public int compareTo(BattleOutcome other) {
            if (this.attackingCasualties != other.attackingCasualties) {
                return Long.compare(this.attackingCasualties, other.attackingCasualties);
            } else {
                return Long.compare(this.defendingCasualties, other.defendingCasualties);
            }
        }

    }

    static {
        singleBattle = new HashMap<>();
        populateSingleBattle();

        battleOutcomes = new HashMap<>();
        populateBasicOutcomes();

        long val = 3;
        for (long i = 1; i < val; i++) {
            for (long j = 1; j < val; j++) {
                getBattleOutcomes(i, j);
            }
        }
    }

    public static void main(String[] args) {
        printOutcomes();
    }

    private static void printOutcomes() {
        for (var attacking : battleOutcomes.entrySet()) {
            for (var defending : attacking.getValue().entrySet()) {
                if (attacking.getKey() == 0 || defending.getKey() == 0) continue;
                System.out.printf("""
                        %d Attacking %d
                        """, attacking.getKey(), defending.getKey());
                for (var outcome : defending.getValue()) {
                    System.out.printf("""
                                Attacking Casualties: %d
                                Defending Casualties: %d
                                Percent Chance:       %.2f%%
                                
                            """,
                            outcome.attackingCasualties,
                            outcome.defendingCasualties,
                            outcome.percentChance * 100
                    );
                }
            }
        }
    }


    public static double getChance(long attackingTroops, long remainingAttackingTroops,
                                   long defendingTroops, long remainingDefendingTroops) {
        return 0.;
    }

    private static Set<BattleOutcome> getBattleOutcomes(long attackingTroops, long defendingTroops) {
        if (battleOutcomes.containsKey(attackingTroops)
                && battleOutcomes.get(attackingTroops).containsKey(defendingTroops)
                && !battleOutcomes.get(attackingTroops).get(defendingTroops).isEmpty()) {
            return battleOutcomes.get(attackingTroops).get(defendingTroops);
        } else if (attackingTroops <= 0 || defendingTroops <= 0) {
            BattleOutcome emptyOutcome = new BattleOutcome(0, 0, 1);
            battleOutcomes.computeIfAbsent(0L, k -> new HashMap<>())
                    .computeIfAbsent(0L, k -> new TreeSet<>(Collections.singletonList(emptyOutcome)));
            return battleOutcomes.get(0L).get(0L);
        }

//        Set<BattleOutcome> outcomes = new TreeSet<>();
//        for (int i = 0; i < attackingTroops; i++) {
//            for (int j = 0; j < defendingTroops; j++) {
//                outcomes.add(new BattleOutcome(i, j, 1));
//            }
//        }
        Map<Long, Map<Long, Long>> AttDefOpts = new HashMap<>();

        List<List<Long>> attackingRolls = getRollList(attackingTroops, true);
        List<List<Long>> defendingRolls = getRollList(defendingTroops, false);
        boolean twoBattles = attackingRolls.get(0).size() > 1 && defendingRolls.get(0).size() > 1;

        for (var attackRollOption : attackingRolls) {
            for (var defendingRollOption : defendingRolls) {
                BattleOutcome fightOutcome = fight(attackRollOption, defendingRollOption, twoBattles);

                long remainingAttackers = attackingTroops - fightOutcome.attackingCasualties;
                long remainingDefenders = defendingTroops - fightOutcome.defendingCasualties;

                if (remainingAttackers == 0 || remainingDefenders == 0) {

                }
//                Set<BattleOutcome> nextOutcomes = calculateBattleOutcomes(remainingAttackers, remainingDefenders);
//
//                for (BattleOutcome next : nextOutcomes) {
//                    if (remainingAttackers == 0 || remainingDefenders == 0) {
//                        outcomes.stream().filter(it -> it.equals(next)).findFirst()
//                                .orElseGet(() -> {
//                                    BattleOutcome ret = new BattleOutcome(
//                                            fightOutcome.attackingCasualties,
//                                            fightOutcome.defendingCasualties,
//                                            0);
//                                    outcomes.add(ret);
//                                    return ret;
//                                })
//                                .percentChance += next.percentChance;
//                    } else {
//
//                    }
//                }
            }
        }


//        battleOutcomes.computeIfAbsent(attackingTroops, k -> new HashMap<>())
//                .put(defendingTroops, outcomes);
//
//        return outcomes;
        return null;
    }

    private static Set<BattleOutcome> getImmediateBattleStats(long attackingTroops,
                                                               long defendingTroops) {
        List<List<Long>> attackingRolls = getRollList(attackingTroops, true);
        List<List<Long>> defendingRolls = getRollList(defendingTroops, false);

        Set<BattleOutcome> options = new TreeSet<>();
        boolean twoBattles = attackingRolls.get(0).size() > 1 && defendingRolls.get(0).size() > 1;
        for (var attackRollOption : attackingRolls) {
            for (var defendingRollOption : defendingRolls) {
                BattleOutcome fightOutcome = fight(attackRollOption, defendingRollOption, twoBattles);

                options.stream().filter(it -> it.equals(fightOutcome)).findFirst()
                        .orElseGet(() -> {
                            var ret = new BattleOutcome(
                                    fightOutcome.attackingCasualties,
                                    fightOutcome.defendingCasualties,
                                    0);
                            options.add(ret);
                            return ret;
                        })
                        .percentChance += fightOutcome.percentChance;
            }
        }
        return options;
    }

    private static BattleOutcome fight(List<Long> attackRollOption, List<Long> defendingRollOption, boolean twoBattles) {
        long attackCasualties = (getMax(defendingRollOption) >= getMax(attackRollOption) ? 1 : 0) +
                               (twoBattles
                                && getSecondMax(defendingRollOption) >= getSecondMax(attackRollOption) ? 1 : 0);
        long defendCasualties = (twoBattles ? 2 : 1) - attackCasualties;
        return singleBattle.get(attackCasualties).get(defendCasualties);
    }

    private static long getMax(List<Long> list) {
        return Collections.max(list);
    }
    private static long getSecondMax(List<Long> list) {
        if (list.size() < 2) throw new RuntimeException("list too small");
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1).findFirst().get();
    }
    private static List<List<Long>> getRollList(long numTroops, boolean isAttacking) {
        long maxLevel = isAttacking ? 3 : 2;
        long level = Math.min(maxLevel, numTroops);
        return getRollList(level);
    }
    private static List<List<Long>> getRollList(long level) {
        if (level > 3 || level < 0) throw new RuntimeException("That's a bad number of levels");
        if (level == 1) {
            return LongStream.rangeClosed(1, 6)
                    .mapToObj(Collections::singletonList)
                    .collect(Collectors.toList());
        } else {
            return getRollList(level - 1).stream()
                    .flatMap(list -> LongStream.rangeClosed(1, 6)
                            .mapToObj(num -> {
                                List<Long> newList = new ArrayList<>(list);
                                newList.add(num);
                                return newList;
                            }))
                    .collect(Collectors.toList());
        }
    }

    private static void populateSingleBattle() {
        LongStream.rangeClosed(0, 2)
                .forEach(l -> singleBattle.put(l, new HashMap<>()));
        LongStream.rangeClosed(0, 2)
                .forEach(l -> LongStream.rangeClosed(0, 2)
                        .forEach(l2 -> singleBattle.get(l)
                                .put(l2, new BattleOutcome(l, l2, 1))));

    }

    private static void populateBasicOutcomes() {
        battleOutcomes.computeIfAbsent(1L, k -> new HashMap<>())
                .computeIfAbsent(1L, k -> new TreeSet<>(Set.of(
                        new BattleOutcome(1, 0, 21./36),
                        new BattleOutcome(0, 1 ,15./36)
                )));
    }
}
