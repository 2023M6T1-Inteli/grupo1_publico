package inteli.cc6.Forms;

import org.apache.commons.math3.util.Pair;
import processing.core.PApplet;
import processing.pdf.PGraphicsPDF;

import java.util.ArrayList;
import java.util.UUID;

public class BobinaPlotter extends PApplet {

    private ArrayList<Pair<Double, ArrayList<Integer>>> _patterns;
    private ArrayList<ArrayList<Integer>> convertedPatterns;
    private ArrayList<Integer> _waste;
    private ArrayList<Double> usageCount;
    private int _jumboSize;
    private int currentIndex = 0;
    private float a = 150;
    private float c = 950;
    private boolean _textCreated = false;
    private String _filePath;

  


    public BobinaPlotter(ArrayList<Pair<Double, ArrayList<Integer>>> result, ArrayList<Integer> uniqueLengthList, int jumboSize, String filePath) {
        _patterns = result;

        this.convertedPatterns = new ArrayList<>();
        this._waste = new ArrayList<>();
        this.usageCount = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            Pair<Double, ArrayList<Integer>> currentResult = result.get(i);
            ArrayList<Integer> convertedPattern = new ArrayList<>();
            int currentPatternLength = 0;
            for (int j = 0; j < currentResult.getSecond().size(); j++) {
                for (int k = currentResult.getSecond().get(j); k > 0; k--) {
                    convertedPattern.add(uniqueLengthList.get(j));
                    currentPatternLength += uniqueLengthList.get(j);
                }
            }
            convertedPatterns.add(convertedPattern);
            _waste.add(jumboSize - currentPatternLength);
            usageCount.add(currentResult.getFirst());
        }
        _jumboSize = jumboSize;
        _filePath = filePath;
    }

    public void settings() {
        noLoop();
        size(1000, 500, PDF, _filePath);
    }

    public void draw() {
        rectMode(CORNERS);
        PGraphicsPDF pdf = (PGraphicsPDF) g;

        for(int i = 0; i < this._patterns.size(); i++) {
            clear();
            drawPattern(convertedPatterns.get(i), 0, usageCount.get(i), this._waste.get(i));
            pdf.nextPage();
        }
        exit();
    }


    public void drawPattern(ArrayList<Integer> pattern, float verticalDisplacement, double usageCount, int waste) {
        fill(255, 255, 255);
        rect(50, 200 + verticalDisplacement, 150, 300 + verticalDisplacement);
        fill(0, 0, 0);
        text("Tir: " + String.format("%.1f", usageCount), 60, 220 + verticalDisplacement);
        text("Resto: " + String.valueOf(waste), 60, 250 + verticalDisplacement);
        text("Bob: " + String.valueOf(pattern.size()), 60, 280 + verticalDisplacement);

        for(int i =0; i < pattern.size(); i++) {
            float finalWidth = calculateWidth(pattern.get(i), 800);
            fill(69, 255, 88);
            rect(a, 200 + verticalDisplacement, a + finalWidth, 300 + verticalDisplacement);
            fill(0, 0, 0);
            textSize(18);
            text(String.valueOf(pattern.get(i)) + "mm", a + 10, 325 + verticalDisplacement);
            a = a + finalWidth;
        }

        float wasteWidth = calculateWidth(Math.round(waste), 800);
        fill(255, 0, 0);
        rect(a, 200 + verticalDisplacement, a + wasteWidth, 300 + verticalDisplacement);
        a = 150;
    }

    private float calculateWidth(Integer format, float rectWidth) {
        float percentage = format * 100 / (float) this._jumboSize;
        float rectFinalWidth = (percentage / 100) * rectWidth;
        return rectFinalWidth;
    }

    public void exit() {
        dispose();
    }
}
