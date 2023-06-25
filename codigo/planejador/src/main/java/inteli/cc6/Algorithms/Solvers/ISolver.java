package inteli.cc6.Algorithms.Solvers;

import inteli.cc6.Models.Result;

import java.util.ArrayList;

public interface ISolver {
    Result solve(double [] objectiveCoefficients, ArrayList<ArrayList<Integer>> patterns, ArrayList<Integer> lengthList, ArrayList<Integer> coilList);
}
