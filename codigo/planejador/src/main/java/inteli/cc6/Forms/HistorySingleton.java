package inteli.cc6.Forms;

import inteli.cc6.Models.Result;

import java.util.LinkedList;
import java.util.Queue;

public class HistorySingleton {

    // creating the singleton structure
    private static HistorySingleton instance;
    private Queue<Result> resultHistory;
    private Result currentResult;

    private HistorySingleton() {
        resultHistory = new LinkedList<>();
    }

    public static HistorySingleton getInstance() {
        if (instance == null) {
            synchronized (HistorySingleton.class) {
                if (instance == null) {
                    instance = new HistorySingleton();
                }
            }
        }
        return instance;
    }

    // returns the history
    public Queue<Result> getHistory() {
        return resultHistory;
    }

    // adds a new result to the history
    // if more than 50 results, remove the oldest one
    public void addResult(Result result) {
        if (resultHistory.size() >= 50) {
            resultHistory.remove();
        }
        resultHistory.add(result);
    }

    // compare a result with the history
    // return the best result
    public Result compareResultByWaste(Result result) {
        Result bestResult = result;
        for (Result r : resultHistory) {
            if (r.getResultWaste() < bestResult.getResultWaste()) {
                bestResult = r;
            }
        }
        return bestResult;
    }

    // set current result
    public void setCurrentResult(Result result) {
        this.currentResult = result;
    }

    // get current result
    public Result getCurrentResult() {
        return currentResult;
    }

    // compare a result with the history
    // return the best result based on the number of patterns
    public Result compareResultByPatterns(Result result) {
        Result bestResult = result;
        for (Result r : resultHistory) {
            if (r.getPatternsSize() < bestResult.getPatternsSize()) {
                bestResult = r;
            }
        }
        return bestResult;
    }

    // get result by id
    public Result getResultById(String id) {
        for (Result r : resultHistory) {
            if (r.getResultId().equals(id)) {
                return r;
            }
        }
        return null;
    }
}
