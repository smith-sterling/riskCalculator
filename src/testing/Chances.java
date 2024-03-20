package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chances {
    //attacking to defending to list of all chances of casualties
    private static Map<Integer, Map<Integer, Map<Stats, Integer>>> exact;
//    private static Map<Integer, Map<Integer, List<Double>>> approx = new HashMap<>();

    private static class Stats {
        int attackingCasualties;
        int defendingCasualties;

        public Stats(int attackingCasualties, int defendingCasualties) {
            this.attackingCasualties = attackingCasualties;
            this.defendingCasualties = defendingCasualties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stats stats = (Stats) o;
            return attackingCasualties == stats.attackingCasualties
                   && defendingCasualties == stats.defendingCasualties;
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
                   '}';
        }
    }

    static {
        exact = new HashMap<>();
        int val = 4;
        for (int i = 1; i < val; i++) {
            for (int j = 1; j < val; j++) {
                attack(i, j);
            }
        }
//        approx = new HashMap<>();
//        System.out.println(getRollList(1));
//        System.out.println(getRollList(2));
//        System.out.println(getRollList(3));
    }

    public static void main(String[] args) {
        printExact();
    }

    private static void printExact() {
        for (var attacking : exact.entrySet()) {
            for (var defending : attacking.getValue().entrySet()) {
                System.out.printf("""
                        %dA vs %dD
                        """, attacking.getKey(), defending.getKey());
                int totalOptions = defending.getValue().values().stream().mapToInt(Integer::intValue).sum();
                for (var stat : defending.getValue().entrySet()) {
                    System.out.printf("""
                                Attacking Casualties: %d
                                Defending Casualties: %d
                                Ways to occur:        %d
                                Percent Chance:       %f
                                
                            """, stat.getKey().attackingCasualties,
                            stat.getKey().defendingCasualties,
                            stat.getValue(),
                            (double) stat.getValue() / totalOptions);
                }
            }
        }
    }

    private static Map<Stats, Integer> attack(int attackingTroops, int defendingTroops) {
        exact.putIfAbsent(attackingTroops, new HashMap<>());
        exact.get(attackingTroops).putIfAbsent(defendingTroops, new HashMap<>());
        if (exact.get(attackingTroops).get(defendingTroops).isEmpty()) {
            return populateAttackStats(attackingTroops, defendingTroops);
        }
        return exact.get(attackingTroops).get(defendingTroops);
    }

    private static Map<Stats, Integer> populateAttackStats(int attackingTroops, int defendingTroops) {
        if (attackingTroops < 1 || defendingTroops < 1) {
            exact.get(attackingTroops).get(defendingTroops).put(new Stats(0, 0), 1);
            exact.get(attackingTroops).get(defendingTroops);
        }
        List<List<Integer>> attackingRolls = getRollList(attackingTroops, true);
        List<List<Integer>> defendingRolls = getRollList(defendingTroops, false);
        Map<Stats, Integer> battleStats = getImmediateBattleStats(attackingRolls, defendingRolls);
        Map<Stats, Integer> moreStats = new HashMap<>();
        for (var entry : battleStats.entrySet()) {

//            getImmediateBattleStats()
//            moreStats.merge()
        }

        exact.get(attackingTroops).put(defendingTroops, battleStats);
        return battleStats;
    }

    private static Map<Stats, Integer> getImmediateBattleStats(List<List<Integer>> attackingRolls,
                                                               List<List<Integer>> defendingRolls) {
        Map<Stats, Integer> options = new HashMap<>();
        boolean twoBattles = attackingRolls.get(0).size() > 1 && defendingRolls.get(0).size() > 1;
        for (var attackRollOption : attackingRolls) {
            for (var defendingRollOption : defendingRolls) {
                options.merge(fight(attackRollOption, defendingRollOption, twoBattles), 1, Integer::sum);
            }
        }
        return options;
    }

    private static Stats fight(List<Integer> attackRollOption, List<Integer> defendingRollOption, boolean twoBattles) {
        int attackCasualties = (getMax(defendingRollOption) >= getMax(attackRollOption) ? 1 : 0) +
                               (twoBattles
                                && getSecondMax(defendingRollOption) >= getSecondMax(attackRollOption) ? 1 : 0);
        int defendCasualties = (twoBattles ? 2 : 1) - attackCasualties;
        return new Stats(attackCasualties, defendCasualties);
    }
    private static int getMax(List<Integer> list) {
        return Collections.max(list);
    }
    private static int getSecondMax(List<Integer> list) {
        if (list.size() < 2) throw new RuntimeException("list too small");
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1).findFirst().get();
    }

    private static List<List<Integer>> getRollList(int numTroops, boolean isAttacking) {
        int maxLevel = isAttacking ? 3 : 2;
        int level = Math.min(maxLevel, numTroops);
        return getRollList(level);
    }

    private static List<List<Integer>> getRollList(int level) {
        if (level > 3 || level < 0) throw new RuntimeException("That's a bad number of levels");
        if (level == 1) {
            return IntStream.rangeClosed(1, 6)
                    .mapToObj(Collections::singletonList)
                    .collect(Collectors.toList());
        } else {
            return getRollList(level - 1).stream()
                    .flatMap(list -> IntStream.rangeClosed(1, 6)
                            .mapToObj(num -> {
                                List<Integer> newList = new ArrayList<>(list);
                                newList.add(num);
                                return newList;
                            }))
                    .collect(Collectors.toList());
        }
    }
}
