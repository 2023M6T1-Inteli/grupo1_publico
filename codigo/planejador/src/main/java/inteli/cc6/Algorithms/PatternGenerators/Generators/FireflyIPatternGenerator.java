package inteli.cc6.Algorithms.PatternGenerators.Generators;

import inteli.cc6.Algorithms.PatternGenerators.IPatternGenerator;
import inteli.cc6.InputReader.InputReader;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * This class implements a firefly algorithm to find the optimal pattern for a
 * coil.
 */

class Firefly implements Comparable<Firefly> {
    ArrayList<Integer> elements;
    Double lightIntensity;

    int patternLength = 0;

    public Firefly(ArrayList<Integer> elements) {
        this.elements = elements;
        this.lightIntensity = 0.0;
    }

    public double updateLightIntensity(ArrayList<Integer> lengths, int maxCoilLength, int maxCoilCount) {
        this.lightIntensity = this._evaluate(lengths, maxCoilLength, maxCoilCount);
        return this.lightIntensity;
    }

    private double _evaluate(ArrayList<Integer> lengths, int maxCoilLength, int maxCoilCount) {
        int sumItems = 0;
        int diverstiy = 0;
        int coilCount = 0;

        for (int i = 0; i < elements.size(); i++) {
            int element = elements.get(i);
            sumItems += element * lengths.get(i);
            if (elements.get(i) > 0) {
                diverstiy++;
            }
            coilCount += element;
        }

        this.patternLength = sumItems;

        if (sumItems > maxCoilLength || coilCount > maxCoilCount) {
            if (coilCount > maxCoilCount) {
                return 0;
            }
            return ((double) sumItems / Math.abs(maxCoilLength - sumItems)) * ((double) diverstiy / maxCoilCount);
        }
        return (double) sumItems;
    }

    int hammingDistance(Firefly other) {
        int distance = 0;
        for (int i = 0; i < elements.size(); i++) {
            if (!elements.get(i).equals(other.elements.get(i))) {
                distance++;
            }
        }
        return distance;
    }

    @Override
    public int compareTo(Firefly o) {
        return this.lightIntensity.compareTo(o.lightIntensity);
    }
}

public class FireflyIPatternGenerator implements IPatternGenerator {
    /**
     * The input reader used to read the coil specifications.
     */
    private InputReader sourceReader;
    final int populationSize;
    final int maxGen;

    /**
     * Creates a new GAIPatternGenerator object.
     *
     * @param sourceReader The input reader used to read the coil specifications.
     */
    public FireflyIPatternGenerator(InputReader sourceReader, int maxGen, int populationSize) {
        this.sourceReader = sourceReader;
        this.populationSize = populationSize;
        this.maxGen = maxGen;
    }

    private Firefly generateRandomFirefly(ArrayList<Integer> lengthList, int maxCoilLength, int maxCoilCount) {
        ArrayList<Integer> elements = new ArrayList<>(lengthList.size());
        for (int i = 0; i < lengthList.size(); i++) {
            elements.add(0);
        }
        ArrayList<Integer> lengthListCopy = new ArrayList<>(lengthList);
        int sumItems = 0;
        int coilCount = 0;
        int allowedMaxCoils = maxCoilCount;
        for (int i = 0; i < maxCoilCount; i++) {
            int randomIndex = (int) (Math.random() * lengthListCopy.size());
            int randomAmount = (int) (Math.random() * (allowedMaxCoils + 1));
            int randomLength = lengthListCopy.get(randomIndex);
            if (randomAmount * randomLength + sumItems > maxCoilLength || coilCount + randomAmount > maxCoilCount) {
                break;
            }
            elements.set(randomIndex, randomAmount);
            sumItems += randomAmount * randomLength;
            coilCount += randomAmount;
            allowedMaxCoils -= randomAmount;
            lengthListCopy.remove(randomIndex);
        }
        Firefly newFirefly = new Firefly(elements);
        newFirefly.updateLightIntensity(lengthList, maxCoilLength, maxCoilCount);
        return newFirefly;
    }

    private ArrayList<Firefly> generateRandomPopulation(int populationSize, ArrayList<Integer> lengthList,
                                                        int maxCoilLength, int maxCoilCount) {
        ArrayList<Firefly> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomFirefly(lengthList, maxCoilLength, maxCoilCount));
        }
        return population;
    }

    private double beta(Firefly I, Firefly J, double beta0, double gamma) {
        int distance = I.hammingDistance(J);
        return beta0 * Math.exp(-gamma * Math.exp(-gamma * Math.pow(distance, 2)));
    }

    public Pair<ArrayList<Integer>, Integer> generatePattern() {
        HashMap<String, Integer> specs = sourceReader.getSetupSpecs();
        int maxCoilLength = specs.get("Larg Max");
        int maxCoilNumber = specs.get("Máx. bob/tirada");

        Set<Integer> uniqueLengths = this.sourceReader.getUniqueCoilLengthsAndAmounts().keySet();
        ArrayList<Integer> uniqueList = new ArrayList<>(uniqueLengths);

        ArrayList<Firefly> fireflyPopulation = generateRandomPopulation(populationSize, uniqueList, maxCoilLength,
                maxCoilNumber);
        Firefly bestFirefly = new Firefly(new ArrayList<>());

        int currentGen = 0;

        while (currentGen < maxGen) {
            for (int i = 0; i < fireflyPopulation.size(); i++) {
                Firefly currentFirefly = fireflyPopulation.get(i);
                for (int j = 0; j < fireflyPopulation.size(); j++) {
                    Firefly otherFirefly = fireflyPopulation.get(j);
                    if (otherFirefly.lightIntensity > currentFirefly.lightIntensity) {
                        double attractiveness = beta(currentFirefly, otherFirefly, 1, 0.2);
                        for (int k = 0; k < currentFirefly.elements.size(); k++) {
                            int currentElement = currentFirefly.elements.get(k);
                            int otherElement = otherFirefly.elements.get(k);
                            if (currentElement == otherElement) {
                                currentFirefly.elements.set(k, otherElement);
                            } else {
                                // random number between 0 and 1
                                double random = Math.random();
                                if (random + 0.5 < attractiveness) {
                                    currentFirefly.elements.set(k, otherElement);
                                } else {
                                    currentFirefly.elements.set(k, currentElement);
                                }
                            }
                        }
                        currentFirefly.updateLightIntensity(uniqueList, maxCoilLength, maxCoilNumber);
                    }
                }
            }
            Firefly best = Collections.max(fireflyPopulation);
            if (currentGen == 0 || best.lightIntensity > bestFirefly.lightIntensity) {
                bestFirefly = best;
            }
            currentGen++;
        }
        System.out.println(uniqueList);
        return new Pair<>(bestFirefly.elements, maxCoilLength - bestFirefly.patternLength);
    }

}
