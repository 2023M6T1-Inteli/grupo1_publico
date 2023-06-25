/**
 * The Greedy class implements a greedy algorithm to calculate the optimal pattern
 * for cutting jumbo rolls based on given lengths.
 */
package inteli.cc6.Algorithms.PatternGenerators.Generators;

import inteli.cc6.InputReader.InputReader;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Greedy {

    private final InputReader sourceReader;

    /**
     * Constructs a Greedy object with the given source reader.
     *
     * @param sourceReader the input reader used to retrieve length lists and setup specifications
     */
    public Greedy(InputReader sourceReader) {
        this.sourceReader = sourceReader;
    }

    /**
     * Calculates the greedy optimal pattern for cutting jumbo rolls based on lengths.
     *
     * @return a pair consisting of the optimal pattern and the remaining length of the jumbo roll
     */
    public Pair<ArrayList<Integer>, Integer> getGreedyOptimal() {

        ArrayList<Integer> lengthList = this.sourceReader.getLengthList();

        // Generate unique length list
        ArrayList<Integer> uniqueList = new ArrayList<>();

        for (int i = 0; i < lengthList.size(); i++) {
            if (!uniqueList.contains(lengthList.get(i))) {
                uniqueList.add(lengthList.get(i));
            }
        }

        // Generate pattern

        ArrayList<Integer> pattern = new ArrayList<>();

        int jumboRoll = sourceReader.getSetupSpecs().get("Larg Max");

        // Randomize list of unique lengths
        Collections.shuffle(lengthList);

        // Knives tracker
        int knivesUsed = 0;

        // Greedy algorithm

        for (int i = 0; i < lengthList.size(); i++) {

            if (knivesUsed == 9) {
                break;
            }

            if (lengthList.get(i) <= jumboRoll) {
                pattern.add(lengthList.get(i));
                jumboRoll = jumboRoll - lengthList.get(i);
                knivesUsed++;
            }
        }

        int sum = 0;
        for (int i : pattern) {
            sum += i;
        }

        sum = sourceReader.getSetupSpecs().get("Larg Max") - sum;

        // Format pattern

        HashMap<Integer, Integer> baseMap = new HashMap<>();
        for (int i = 0; i < uniqueList.size(); i++) {
            baseMap.put(uniqueList.get(i), 0);
        }

        ArrayList<Integer> formattedPattern = new ArrayList<>(convertPatternToFormatCount(pattern, baseMap));

        return new Pair<>(formattedPattern, sum);
    }

    /**
     * Converts the pattern list into a formatted count list based on the base map.
     *
     * @param pattern  the pattern list
     * @param baseMap  the base map containing unique lengths
     * @return an ArrayList representing the formatted count list
     */
    private ArrayList<Integer> convertPatternToFormatCount(List<Integer> pattern, HashMap<Integer, Integer> baseMap) {
        HashMap<Integer, Integer> formatCount = new HashMap<>(baseMap);
        for (Integer integer : pattern) {
            if (formatCount.containsKey(integer)) {
                formatCount.put(integer, formatCount.get(integer) + 1);
            } else {
                formatCount.put(integer, 1);
            }
        }
        return new ArrayList<>(formatCount.values());
    }
}
