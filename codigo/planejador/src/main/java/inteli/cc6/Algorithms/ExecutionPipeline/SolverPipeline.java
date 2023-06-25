package inteli.cc6.Algorithms.ExecutionPipeline;

import inteli.cc6.Algorithms.PatternGenerators.IPatternGenerator;
import inteli.cc6.Algorithms.Solvers.ISolver;
import inteli.cc6.InputReader.InputReader;
import inteli.cc6.Models.Result;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The SolverPipeline class represents a pipeline of solvers that can be used to solve an optimization problem.
 * It generates patterns and their corresponding waste values, retrieves the unique coil lengths and amounts from the source reader,
 * and solves the optimization problem using each solver in the ISolvers array.
 */
public class SolverPipeline {
    private final InputReader sourceReader;
    private final IPatternGenerator IPatternGenerator;
    private JProgressBar progressBar = null;
    private ArrayList<ISolver> ISolvers = new ArrayList<>();
    private final int maxLength;

    private int progress = 0;
    private int maxProgress = 0;

    public boolean retryOnFailure = false;
    public int maxRetries = 10;

    /**
     * Constructs a SolverPipeline object with the given InputReader and IPatternGenerator objects.
     * @param sourceReader the InputReader object to read the input data from
     * @param IPatternGenerator the IPatternGenerator object to generate patterns
     */
    public SolverPipeline(InputReader sourceReader, IPatternGenerator IPatternGenerator) {
        this.sourceReader = sourceReader;
        this.IPatternGenerator = IPatternGenerator;
        this.maxLength = sourceReader.getSetupSpecs().get("Larg Max");
    }

    /**
     * Adds a solver to the pipeline.
     * @param ISolver the ISolver object to add to the pipeline
     */
    public void addSolver(ISolver ISolver) {
        this.ISolvers.add(ISolver);
    }

    /**
     * Solves the optimization problem using the given ISolver object and Result object.
     * @param ISolver the ISolver object to use to solve the optimization problem
     * @param result the Result object to use to calculate the objective coefficients and patterns
     * @param uniqueLengthList the list of unique coil lengths
     * @param uniqueCoilList the list of unique coil amounts
     * @return the Result object containing the solution to the optimization problem
     */
    private Result solveFromResult(ISolver ISolver, Result result, ArrayList<Integer> uniqueLengthList, ArrayList<Integer> uniqueCoilList) {
        // Calculate the objective coefficients and patterns for the given Result object
        ArrayList<Pair<Double, ArrayList<Integer>>> resultPatterns = result.getPatterns();
        double[] objectiveCoefficients = new double[resultPatterns.size()];
        ArrayList<ArrayList<Integer>> patterns = new ArrayList<>();

        for (int i = 0; i < resultPatterns.size(); i++) {
            int currentLength = 0;
            for (int j = 0; j < resultPatterns.get(i).getValue().size(); j++) {
                currentLength += resultPatterns.get(i).getValue().get(j) * uniqueLengthList.get(j);
            }

            objectiveCoefficients[i] = maxLength - currentLength;
            patterns.add(resultPatterns.get(i).getValue());
        }

        // Solve the optimization problem using the calculated objective coefficients, patterns, and unique coil lengths and amounts
        return ISolver.solve(objectiveCoefficients, patterns, uniqueLengthList, uniqueCoilList);
    }

    /**
     * Sets the progress bar for the pipeline.
     * @param progressBar the JProgressBar object to set as the progress bar
     */
    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Generates patterns and their corresponding waste values.
     * @param iterations the number of patterns to generate
     * @param objectiveCoefficients the array to store the waste values in
     * @return the list of generated patterns
     */
    private ArrayList<ArrayList<Integer>> generatePatterns(int iterations, double[] objectiveCoefficients) {
        // Generate patterns and their corresponding waste values
        ArrayList<ArrayList<Integer>> generatedPatterns = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            Pair<ArrayList<Integer>, Integer> generatedPatternPair = IPatternGenerator.generatePattern();
            ArrayList<Integer> pattern = generatedPatternPair.getKey();
            double waste = generatedPatternPair.getValue();

            objectiveCoefficients[i] = waste;
            generatedPatterns.add(pattern);
            setProgress(progress + 1);
        }
        return generatedPatterns;
    }

    /**
     * Sets the progress of the pipeline.
     * @param value the value to set the progress to
     */
    private void setProgress(int value) {
        this.progress = value;
        float percentage = (float) progress / (float) maxProgress;
        if (progressBar != null) {
            progressBar.setValue(Math.round(percentage * 100));
        }
    }

    /**
     * Runs the pipeline to solve the optimization problem.
     * @param iterations the number of patterns to generate
     * @return the Result object containing the solution to the optimization problem
     */
    public Result run(int iterations) {
        boolean success = false;

        try {
            this.maxProgress = iterations + ISolvers.size();

            // Generate patterns and their corresponding waste values
            double[] objectiveCoefficients = new double[iterations];
            ArrayList<ArrayList<Integer>> generatedPatterns = generatePatterns(iterations, objectiveCoefficients);

            // Retrieve the unique coil lengths and amounts from the source reader
            HashMap<Integer, Integer> demandMap = sourceReader.getUniqueCoilLengthsAndAmounts();
            ArrayList<Integer> uniqueLengthList = new ArrayList<>(demandMap.keySet());
            ArrayList<Integer> uniqueCoilList = new ArrayList<>(demandMap.values());

            // Solve the optimization problem using each solver in the ISolvers array
            Result result = null;
            for (ISolver ISolver : ISolvers) {
                if (result != null) {
                    result = solveFromResult(ISolver, result, uniqueLengthList, uniqueCoilList);
                } else {
                    result = ISolver.solve(objectiveCoefficients, generatedPatterns, uniqueLengthList, uniqueCoilList);
                }
                setProgress(progress + 1);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}