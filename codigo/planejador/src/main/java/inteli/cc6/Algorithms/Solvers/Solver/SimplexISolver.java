/**
 * The SimplexISolver class implements the simplex algorithm to solve a linear programming problem
 * and find the optimal solution with minimized waste.
 */
package inteli.cc6.Algorithms.Solvers.Solver;

import inteli.cc6.Algorithms.Solvers.ISolver;
import org.apache.commons.math3.optim.MaxIter;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.util.Pair;

import inteli.cc6.Models.Result;

import java.util.ArrayList;
import java.util.UUID;

public class SimplexISolver implements ISolver {

    private double _waste;

    /**
     * Solves the linear programming problem using the simplex algorithm and returns the result.
     *
     * @return the result of the optimization process, including the optimal pattern and minimized waste
     */
    public Result solve(double [] objectiveCoefficients, ArrayList<ArrayList<Integer>> patterns, ArrayList<Integer> lengthList, ArrayList<Integer> coilList) {
        if (objectiveCoefficients == null || patterns == null || lengthList == null || coilList == null)
            throw new IllegalArgumentException();

        ArrayList<Pair<Double, ArrayList<Integer>>> rawResult = new ArrayList<>();

        // Define the objective function
        double constantTerm = 0;
        LinearObjectiveFunction objective = new LinearObjectiveFunction(objectiveCoefficients, constantTerm);

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraintSolver(patterns, lengthList, coilList));

        // Solve the problem
        org.apache.commons.math3.optim.linear.SimplexSolver solver = new org.apache.commons.math3.optim.linear.SimplexSolver();
        PointValuePair solution = solver.optimize(
                new MaxIter(400), objective, constraintSet, GoalType.MINIMIZE, new NonNegativeConstraint(true));

        for (int i = 0; i < patterns.size(); i++) {
            if (solution.getPointRef()[i] > 0) {
                rawResult.add(new Pair<>(solution.getPointRef()[i], patterns.get(i)));
            }
        }
        setWaste(solution.getValue());

        UUID randomId = UUID.randomUUID();

        return new Result(randomId.toString(), rawResult, solution.getValue());
    }

    /**
     * Generates the constraints for the linear programming problem based on length and coil amounts.
     *
     * @return an array of linear constraints
     */
    private LinearConstraint[] constraintSolver(ArrayList<ArrayList<Integer>> patterns, ArrayList<Integer> lengthList, ArrayList<Integer> coilList) {
        LinearConstraint[] constraints = new LinearConstraint[lengthList.size()];

        for (int i = 0; i < lengthList.size(); i++) {
            double[] nonNegativity = new double[lengthList.size()];

            double[] amounts = new double[patterns.size()];
            int coilAmount = coilList.get(i);

            for (int k = 0; k < patterns.size(); k++) {
                amounts[k] += patterns.get(k).get(i);
            }

            constraints[i] = new LinearConstraint(amounts, Relationship.GEQ, coilAmount);
        }
        return constraints;
    }

    /**
     * Sets the waste value.
     *
     * @param waste the value of the waste
     */
    private void setWaste(double waste) {
        _waste = waste;
    }

    /**
     * Returns the waste value.
     *
     * @return the value of the waste
     */
    public double getWaste() {
        return _waste;
    }

}
