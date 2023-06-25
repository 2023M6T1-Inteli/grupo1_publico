package inteli.cc6.Algorithms.PatternGenerators;

import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;

public interface IPatternGenerator {
    Pair<ArrayList<Integer>, Integer> generatePattern();
}
