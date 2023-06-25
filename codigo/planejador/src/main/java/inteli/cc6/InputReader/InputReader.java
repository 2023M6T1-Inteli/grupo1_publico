package inteli.cc6.InputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface InputReader {
    ArrayList<Integer> getLengthList();

    ArrayList<Integer> getCoilList();

    ArrayList<Integer> getPriorityList();

    HashMap<String, Integer> getSetupSpecs();

    public HashMap<Integer, Integer> getUniqueCoilLengthsAndAmounts();
}