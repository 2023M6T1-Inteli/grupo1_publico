package inteli.cc6.Algorithms.Solvers.Solver;

import java.util.ArrayList;
import java.util.UUID;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import inteli.cc6.Algorithms.Solvers.ISolver;
import inteli.cc6.Models.Result;
import org.apache.commons.math3.util.Pair;

/**
 * This class represents a mixed-integer programming solver using the SCIP backend.
 * It solves a specific optimization problem based on the given objective coefficients,
 * patterns, length list, and coil list.
 */
public class MIPISolver implements ISolver {

    public Result solve(double [] objectiveCoefficients, ArrayList<ArrayList<Integer>> patterns, ArrayList<Integer> lengthList, ArrayList<Integer> coilList) {
        if (objectiveCoefficients == null || patterns == null || lengthList == null || coilList == null)
            throw new IllegalArgumentException();

        ArrayList<Pair<Double, ArrayList<Integer>>> rawResult = new ArrayList<>();


        // Load the native library.
        Loader.loadNativeLibraries();

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new RuntimeException("Could not create solver SCIP");
        }

        if (objectiveCoefficients.length != patterns.size()) {
            throw new IllegalArgumentException("Objective coefficients and patterns size mismatch");
        }
        if (lengthList.size() != coilList.size()) {
            throw new IllegalArgumentException("Length and coil list size mismatch");
        }

        int largeNumber = 9999;

        // Decision variables - how many times each pattern is used
        MPVariable[] x = new MPVariable[patterns.size()];
        for (int i = 0; i < patterns.size(); i++) {
            x[i] = solver.makeIntVar(0.0, largeNumber, "pattern" + i);
        }

        // Decision variables - penalty for using each pattern
        MPVariable[] p = new MPVariable[patterns.size()];
        for (int i = 0; i < patterns.size(); i++) {
            p[i] = solver.makeIntVar(0.0, largeNumber, "penalty" + i);
        }


        for (int i = 0; i < lengthList.size(); i++) {
            int demand = coilList.get(i);
            MPConstraint constraint = solver.makeConstraint(demand, largeNumber);
            for (int j = 0; j < patterns.size(); j++) {
                constraint.setCoefficient(x[j], patterns.get(j).get(i));
            }
        }


        // Objective function - Minimum waste
        MPObjective objective = solver.objective();
        for (int i = 0; i < patterns.size(); i++) {
            objective.setCoefficient(x[i], objectiveCoefficients[i]);
            objective.setCoefficient(p[i], 1.0 / patterns.size());
        }

        objective.setMinimization();


        // Solve
        MPSolver.ResultStatus resultStatus = solver.solve();

        if(resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            throw new RuntimeException("The given problem does not have an optimal solution");
        }

        for (int i = 0; i < patterns.size(); i++) {
            if (x[i].solutionValue() > 0) {
                rawResult.add(new Pair<> (x[i].solutionValue(),patterns.get(i)));

            }
        }

        UUID randomId = UUID.randomUUID();
        Result result = new Result(randomId.toString(), rawResult, objective.value());

        return result;
    }

}
