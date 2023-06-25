package inteli.cc6.Algorithms.PatternGenerators.Generators;

import inteli.cc6.Algorithms.PatternGenerators.IPatternGenerator;
import inteli.cc6.Forms.Singleton;
import inteli.cc6.InputReader.InputReader;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class implements a genetic algorithm to find the optimal pattern for a
 * coil.
 *
 */
public class GAIPatternGenerator implements IPatternGenerator {
    /**
     * The input reader used to read the coil specifications.
     */
    private InputReader sourceReader;

    /**
     * The population size.
     */
    Singleton singleton = Singleton.getInstance();
    int population = singleton.getPopulationSize();
    private int populationSize = population;

    /**
     * Creates a new GAIPatternGenerator object.
     *
     * @param sourceReader The input reader used to read the coil specifications.
     */
    public GAIPatternGenerator(InputReader sourceReader, int populationSize) {
        this.sourceReader = sourceReader;
        this.populationSize = populationSize;
    }

    /**
     *
     * Evaluates the fitness of a pattern.
     * 
     * @param pattern       The pattern to evaluate.
     * @param maxCoilLength The maximum length of a coil.
     * @return The fitness of the pattern.
     */
    private int evaluatePattern(ArrayList<Integer> pattern, int maxCoilLength) {
        // sums the length of all coils in the pattern
        int sum = 0;
        for (int i = 0; i < pattern.size(); i++) {
            sum += pattern.get(i);
        }
        // if the sum is greater than the max length, the pattern is invalid
        if (sum > maxCoilLength) {
            return -1;
        }

        return sum;

    }

    /**
     * Crosses over two patterns to create two new patterns.
     *
     * @param parent1 The first parent pattern.
     * @param parent2 The second parent pattern.
     * @return The two new patterns.
     */
    private ArrayList<ArrayList<Integer>> crossOver(ArrayList<Integer> parent1, ArrayList<Integer> parent2) {
        ArrayList<ArrayList<Integer>> children = new ArrayList<>();

        // get the size of the parents
        int size = parent1.size();

        // get a random index to split the parents
        int splitIndex = (int) (Math.random() * size);

        // create the children
        ArrayList<Integer> child1 = new ArrayList<>();
        ArrayList<Integer> child2 = new ArrayList<>();

        // fill the children with the first part of the parents
        for (int i = 0; i < splitIndex; i++) {
            child1.add(parent1.get(i));
            child2.add(parent2.get(i));
        }

        // fill the children with the second part of the parents
        for (int i = splitIndex; i < size; i++) {
            child1.add(parent2.get(i));
            child2.add(parent1.get(i));
        }

        // add the children to the list
        children.add(child1);
        children.add(child2);

        return children;
    }

    /**
     * Checks if a population of patterns is valid.
     *
     * @param population    The population of patterns to check.
     * @param maxCoilLength The maximum length of a coil.
     * @return True if the population is valid, false otherwise.
     */
    private boolean isValidPopulation(ArrayList<ArrayList<Integer>> population, int maxCoilLength) {
        // check if the population is valid
        for (int i = 0; i < population.size(); i++) {
            if (evaluatePattern(population.get(i), maxCoilLength) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Evaluates the fitness of a population of patterns.
     *
     * @param population    The population of patterns to evaluate.
     * @param maxCoilLength The maximum length of a coil.
     * @return The fitness of the population.
     */
    private ArrayList<Integer> evaluateFitness(ArrayList<ArrayList<Integer>> population, int maxCoilLength) {
        // create a list to store the fitness of each pattern
        ArrayList<Integer> fitness = new ArrayList<>();

        // evaluate the fitness of each pattern
        for (int i = 0; i < population.size(); i++) {
            fitness.add(evaluatePattern(population.get(i), maxCoilLength));
        }

        return fitness;
    }

    /**
     * Selects the best patterns from a population.
     *
     * @param population    The population of patterns to select from.
     * @param maxCoilLength The maximum length of a coil.
     * @param k             The number of best patterns to select.
     * @return The best patterns.
     */
    private ArrayList<ArrayList<Integer>> selectBestPatterns(ArrayList<ArrayList<Integer>> population,
            int maxCoilLength, int k) {
        // create a list to store the best patterns
        ArrayList<ArrayList<Integer>> bestPatterns = new ArrayList<>();

        // get fitness
        ArrayList<Integer> fitness = evaluateFitness(population, maxCoilLength);

        for (int i = 0; i < k; i++) {
            // get the index of the best pattern
            int bestIndex = fitness.indexOf(fitness.stream().max(Integer::compare).get());

            // add the best pattern to the list
            bestPatterns.add(population.get(bestIndex));
        }

        return bestPatterns;
    }

    /**
     * Generates a new population of patterns from a population.
     *
     * @param population The population of patterns to generate from.
     * @return The new population.
     */
    private ArrayList<ArrayList<Integer>> generateNewPopulation(ArrayList<ArrayList<Integer>> population) {
        ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            int tenPercentPopulation = (int) (this.populationSize * 0.1);
            for (int j = 0; j < tenPercentPopulation; j++) {
                int randomParent1Index = (int) (Math.random() * population.size());
                int randomParent2Index = (int) (Math.random() * population.size());
                ArrayList<ArrayList<Integer>> children = crossOver(population.get(randomParent1Index),
                        population.get(randomParent2Index));
                newPopulation.add(children.get(0));
                newPopulation.add(children.get(1));
            }
        }
        return newPopulation;
    }

    /**
     * Gets the optimal pattern for a coil.
     *
     * @return The optimal pattern.
     */
    public Pair<ArrayList<Integer>, Integer> generatePattern() {
        HashMap<String, Integer> specs = sourceReader.getSetupSpecs();
        int maxCoilLength = specs.get("Larg Max");
        int maxCoilNumber = specs.get("Máx. bob/tirada");

        ArrayList<ArrayList<Integer>> population = new ArrayList<>();
        Set<Integer> lengthSet = this.sourceReader.getUniqueCoilLengthsAndAmounts().keySet();
        ArrayList<Integer> uniqueList = new ArrayList<>(lengthSet);

        for (int i = 0; i < this.populationSize; i++) {
            ArrayList<Integer> pattern = new ArrayList<>();
            int sum = 0;
            for (int j = 0; j < maxCoilNumber; j++) {
                int positionIndex = (int) (Math.random() * uniqueList.size());
                if (sum + uniqueList.get(positionIndex) > maxCoilLength) {
                    break;
                }
                pattern.add(uniqueList.get(positionIndex));
                sum += uniqueList.get(positionIndex);
            }
            population.add(pattern);
        }
        int tenPercentPopulation = (int) (this.populationSize * 0.1);
        ArrayList<ArrayList<Integer>> bestPatterns = selectBestPatterns(population, maxCoilLength,
                tenPercentPopulation);
        ArrayList<ArrayList<Integer>> newPopulation = generateNewPopulation(bestPatterns);
        population = newPopulation;

        while (!isValidPopulation(population, maxCoilLength)) {
            bestPatterns = selectBestPatterns(population, maxCoilLength, tenPercentPopulation);
            newPopulation = generateNewPopulation(bestPatterns);
            population = newPopulation;
        }

        HashMap<Integer, Integer> baseMap = new HashMap<>();
        for (int i = 0; i < uniqueList.size(); i++) {
            baseMap.put(uniqueList.get(i), 0);
        }

        ArrayList<Integer> bestPattern = selectBestPatterns(population, maxCoilLength, 1).get(0);
        ArrayList<Integer> formatCount = convertPatternToFormatCount(bestPattern, baseMap, uniqueList);
        //System.out.println(uniqueList);
        return new Pair<>(formatCount, maxCoilLength - evaluatePattern(bestPattern, maxCoilLength));
    }

    /**
     * Converts a pattern to a format count.
     *
     * @param pattern The pattern to convert.
     * @param baseMap The base map.
     * @return The format count.
     */
    private ArrayList<Integer> convertPatternToFormatCount(ArrayList<Integer> pattern,
            HashMap<Integer, Integer> baseMap, ArrayList<Integer> referenceList) {
        HashMap<Integer, Integer> formatCount = new HashMap<>(baseMap);
        for (int i = 0; i < pattern.size(); i++) {
            if (formatCount.containsKey(pattern.get(i))) {
                formatCount.put(pattern.get(i), formatCount.get(pattern.get(i)) + 1);
            } else {
                formatCount.put(pattern.get(i), 1);
            }
        }
        ArrayList<Integer> formatCountList = new ArrayList<>();
        for (int i = 0; i < referenceList.size(); i++) {
            formatCountList.add(formatCount.get(referenceList.get(i)));
        }
        return formatCountList;
    }

}
