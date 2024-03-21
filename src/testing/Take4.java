package testing;

import utils.Pair;
import utils.Quad;
import utils.Triple;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static utils.Pair.p;

public class Take4 {

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

    private static final Comparator<Pair> comPAIRator = (first, second) -> {
        int firstComparison = Integer.compare(first.first, second.first);
        if (firstComparison != 0) {
            return -firstComparison;
        }
        return Integer.compare(first.second, second.second);
    };

    private static final List<Pair> twoCasOps = List.of(p(2, 0), p(1, 1), p(0, 2));
    private static final List<Pair> oneCasOps = List.of(p(1, 0), p(0, 1));

    private static final Map<Quad, Double> ops;

    private static final Map<Pair, Map<Pair, Double>> battleChances = new TreeMap<>(comPAIRator);


    static {
        ops = new HashMap<>();
        ops.putAll(Map.of(
                new Quad(1, 1, 1, 0), MAGIC_1_1_TO_1_0,
                new Quad(1, 1, 0, 1), MAGIC_1_1_TO_0_1,
                new Quad(1, 2, 0, 2), MAGIC_1_2_TO_0_2,
                new Quad(1, 2, 1, 1), MAGIC_1_2_TO_1_1,
                new Quad(2, 1, 2, 0), MAGIC_2_1_TO_2_0,
                new Quad(2, 1, 1, 1), MAGIC_2_1_TO_1_1,
                new Quad(3, 1, 3, 0), MAGIC_3_1_TO_3_0,
                new Quad(3, 1, 2, 1), MAGIC_3_1_TO_2_1
                ));
        ops.putAll(Map.of(
                new Quad(2, 2, 2, 0), MAGIC_2_2_TO_2_0,
                new Quad(2, 2, 0, 2), MAGIC_2_2_TO_0_2,
                new Quad(2, 2, 1, 1), MAGIC_2_2_TO_1_1,
                new Quad(3, 2, 3, 0), MAGIC_3_2_TO_3_0,
                new Quad(3, 2, 2, 1), MAGIC_3_2_TO_2_1,
                new Quad(3, 2, 1, 2), MAGIC_3_2_TO_1_2
        ));
    }

    private static int calcDeaths(int a, int b) {
        if (a <= 0 || b <= 0) {
            return 0;
        } else if (b == 1 || a == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    private static double chanceInOut(int ain, int bin, int aout, int bout) {
        Quad asdf = new Quad (
                min(3, ain),
                min(2, bin),
                min(3, ain) - (ain - aout),
                min(2, bin) - (bin - bout)
        );
        return ops.getOrDefault(asdf, 0.);
    }

    private static Map<Pair, Double> outer(int a, int b) {
        if (battleChances.get(p(a, b)) == null)
            battleChances.put(p(a, b), inner(a, b));
        return battleChances.get(p(a, b));
    }

    public static Map<Pair, Double> inner(int a, int b) {
        Map<Pair, Double> options = new TreeMap<>(comPAIRator);
        ArrayDeque<Triple> queue = new ArrayDeque<>();
        queue.add(new Triple(a, b, 1.));

        while (!queue.isEmpty()) {
            Triple curr = queue.pop();
            int acurr = curr.first, bcurr = curr.second;
            double ccurr = curr.third;

            Pair p = p(acurr, bcurr);
            if (options.containsKey(p)) {
                options.put(p, options.get(p) + ccurr); // Update probability by adding
            } else {
                options.put(p, ccurr);
            }

            if (acurr <= 0 || bcurr <= 0) continue; // Skip further calculations if one of the troops is depleted
            int deaths = calcDeaths(acurr, bcurr);
            if (deaths == 0) continue; // No deaths, nothing to calculate
            for (var cas : deaths == 1 ? oneCasOps : twoCasOps) {
                double probNext = ccurr * chanceInOut(acurr, bcurr, acurr - cas.first, bcurr - cas.second);
                queue.stream().filter(it -> it.first == acurr - cas.first && it.second == bcurr - cas.second)
                                .findFirst()
                                .ifPresentOrElse(it -> it.third += probNext,
                                                () -> queue.add(new Triple(acurr - cas.first, bcurr - cas.second, probNext)));
            }
        }

        return options;
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("""
                Please enter two positive integers for
                    the number of attacking troops and
                    the number of defending troops
                    for Risk, and we will calculate the chance of every outcome
                """);
        while (true) {
            System.out.println();
            String a = s.next();
            if (a.equalsIgnoreCase("quit") || a.equalsIgnoreCase("exit")) break;
            String b = s.next();
            if (a.equalsIgnoreCase("quit") || a.equalsIgnoreCase("exit")) break;
            try {
                int i = Integer.parseInt(a), j = Integer.parseInt(b);
                if (i <= 0 || j <= 0) throw new NumberFormatException("Bad number");
                printOutcomes(outer(i, j));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String ATTACKING = "\u001B[36m";
    private static final String DEFENDING = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static String getPercentageString(double percentage) {
        String something =
                percentage > 45 ? "\u001B[38;5;46m" :
                percentage > 20 ? "\u001B[38;5;40m" :
                percentage > 10 ? "\u001B[38;5;34m" :
                percentage > 3 ? "\u001B[38;5;28m" : "\u001B[38;5;22m";
        return String.format("%s%.2f%%%s", something, percentage, RESET);
    }

    private static void printAllOutcomes() {
        for (var map : battleChances.entrySet()) {
            printOutcomes(map.getValue());
        }
    }
    private static void printOutcomes(Map<Pair, Double> map) {
        Map<Pair, Double> finalMap = map.entrySet().stream()
                .filter(it -> !(it.getKey().first > 0 && it.getKey().second > 0))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (o1, o2) -> o1, //for collisions
                        () -> new TreeMap<>(comPAIRator)
                ));

        printVersusString(finalMap);
        printWinChances(finalMap);

        printRetainTroopStats(finalMap);

        if (finalMap.size() <= 50) {
            // TODO: 3/20/24 make a cumulative detailed stats if some global flag is set from input
            printDetailedStats(finalMap);
        }

    }

    private static void printVersusString(Map<Pair, Double> map) {
        System.out.printf("""
                    
                    %s%d%s vs %s%d%s
                    """,
                ATTACKING, map.keySet().stream().max(Comparator.comparingInt(it -> it.first)).get().first, RESET,
                DEFENDING, map.keySet().stream().max(Comparator.comparingInt(it -> it.second)).get().second, RESET
        );
    }

    private static void printWinChances(Map<Pair, Double> map) {
        System.out.printf("""
                    %s %sAttackers%s and %s %sDefenders%s
                """,
                getPercentageString(map.entrySet().stream()
                                            .filter(it -> it.getKey().second.equals(0))
                                            .mapToDouble(Map.Entry::getValue)
                                            .sum() * 100),
                ATTACKING, RESET,
                getPercentageString(map.entrySet().stream()
                                            .filter(it -> it.getKey().first.equals(0))
                                            .mapToDouble(Map.Entry::getValue)
                                            .sum() * 100),
                DEFENDING, RESET
        );
    }

    private static void printRetainTroopStats(Map<Pair, Double> map) {
        boolean attackersWin = isAttackersWin(map);

        double sum = 0.0;
        int numTroops = Integer.MAX_VALUE;
        Map<Pair, Double> winners = map.entrySet().stream()
                .filter(it -> (attackersWin) ? it.getKey().first > 0 : it.getKey().second > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1,
                        () -> new TreeMap<>(comPAIRator)));
        for (var entry : winners.entrySet()) {
            sum += entry.getValue();
            numTroops = Math.min(numTroops, (attackersWin) ? entry.getKey().first : entry.getKey().second);
            if (sum > 0.5) {
                break;
            }
        }

        System.out.printf("""
                    %s chance %s will retain at least %s%d%s troops
                """,
                getPercentageString(sum * 100),
                attackersWin
                        ? ATTACKING + "Attackers" + RESET
                        : DEFENDING + "Defenders" + RESET,
                attackersWin ? ATTACKING : DEFENDING, numTroops, RESET
        );
    }

    private static boolean isAttackersWin(Map<Pair, Double> map) {
        return map.entrySet().stream()
                       .filter(it -> it.getKey().first > 0)
                       .mapToDouble(Map.Entry::getValue).sum()
               >
               map.entrySet().stream()
                       .filter(it -> it.getKey().second > 0)
                       .mapToDouble(Map.Entry::getValue).sum();
    }

    private static void printDetailedStats(Map<Pair, Double> map) {
        for (var after : map.entrySet()) {
            System.out.printf("""
                            %s\t--\t%s%d%s vs %s%d%s
                        """,
                    getPercentageString(after.getValue() * 100),
                    ATTACKING, after.getKey().first, RESET,
                    DEFENDING, after.getKey().second, RESET
            );
        }
    }
}
