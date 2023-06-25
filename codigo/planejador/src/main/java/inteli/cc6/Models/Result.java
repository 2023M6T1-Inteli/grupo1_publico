package inteli.cc6.Models;

import java.util.ArrayList;

import org.apache.commons.math3.util.Pair;

public class Result {
    private final String resultId;
    private final double resultWaste;
    private final ArrayList<Pair<Double, ArrayList<Integer>>> patterns;

    public Result(String resultId, ArrayList<Pair<Double, ArrayList<Integer>>> result, double waste) {
        this.resultId = resultId;
        this.resultWaste = waste;
        this.patterns = result;
    }

    public String getResultId() {
        return resultId;
    }

    public double getResultWaste() {
        return resultWaste;
    }

    public ArrayList<Pair<Double, ArrayList<Integer>>> getPatterns() {
        return patterns;
    }

    public int getPatternsSize() {
        return patterns.size();
    }

}
