package inteli.cc6.InputReader.Readers;

import inteli.cc6.InputReader.InputReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * The Excel class implements the InputReader interface and reads data from an
 * Excel spreadsheet.
 *
 */

public class Excel implements InputReader {
    private final String _filePath;
    private ArrayList<Integer> _lengthList;
    private ArrayList<Integer> _coilList;
    private ArrayList<Integer> _priorityList;
    private HashMap<String, Integer> _setupSpecs;

    /**
     * Constructs a new Excel object and initializes the path to the Excel
     * spreadsheet.
     *
     * @param filePath The path to the Excel spreadsheet.
     */
    public Excel(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException();
        }
        this._filePath = filePath;
        this._lengthList = new ArrayList<>();
        this._coilList = new ArrayList<>();
        this._priorityList = new ArrayList<>();
        this._setupSpecs = new HashMap<>();

    }

    /**
     * Finds the last non-empty row in the specified sheet.
     *
     * @param sheet The sheet to search.
     * @return The index of the last non-empty row.
     */
    private int findLastNonEmptyRow(Sheet sheet) {
        int foundLastRow = -1;
        for (int l = 8; l <= sheet.getLastRowNum(); l++) {
            Row row = sheet.getRow(l);
            if (row != null) {
                for (Cell cell : row) {
                    if (cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType._NONE) {
                        foundLastRow = l;
                        break;
                    }
                }
                if (row.getRowNum() != foundLastRow) {
                    break;
                }
            }
        }
        return foundLastRow;
    }

    /**
     * Reads the specified sheet in the Excel spreadsheet and populates the length,
     * coil, and priority lists.
     *
     * @param sheetNumber The index of the sheet to read.
     */
    public void readSheet(int sheetNumber) {
        List<String> specName = new ArrayList<>();
        List<Integer> specValue = new ArrayList<>();
        int specNameRow = 3;
        int specValueRow = 4;

        try (FileInputStream fis = new FileInputStream(new File(this._filePath));
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetNumber); // Get the first sheet
            int lastRow = findLastNonEmptyRow(sheet);
            for (Cell cl : sheet.getRow(specNameRow)) {
                String cellValue = getCellValue(cl);
                specName.add(cellValue);
            }

            for (Cell cl : sheet.getRow(specValueRow)) {
                String cellValue = getCellValue(cl);
                specValue.add(parseInt(cellValue));
            }

            int i = 0;
            while (i < specValue.size()) {
                _setupSpecs.put(specName.get(i), specValue.get(i));
                i++;
            }

            int priorityCol = 3;
            int lengthCol = 4;
            int coilCol = 5;
            int rowStart = 8;
            int rowEnd = lastRow;
            int[] cols = { priorityCol, lengthCol, coilCol };

            for (int k : cols) {
                int j = rowStart;
                while (j <= rowEnd) {
                    int cellValue = parseInt(getCellValue(sheet.getRow(j).getCell(k)).replace(".", ""));
                    if (k == priorityCol)
                        _priorityList.add(cellValue);
                    else if (k == lengthCol)
                        _lengthList.add(cellValue);
                    else if (k == coilCol)
                        _coilList.add(cellValue);
                    j++;
                }

            }
            testParams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the setup specifications.
     */
    private void testParams() {

        String[] specNames = { "Larg Min", "Larg Max", "Máx. bob/tirada", "Tiradas múltiplas",
                "Pontas, larg. inferior a:", "Máximo de Pontas/ tirada" };

    }

    /**
     * Gets the cell value as a string.
     *
     * @param cell The cell to get the value from.
     * @return The cell value as a string.
     */
    private static String getCellValue(Cell cell) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }

    /**
     * Gets the list of the lengths of the coils in the spreadsheet.
     *
     * @return The list of the lengths of the coils in the spreadsheet.
     */
    public ArrayList<Integer> getLengthList() {
        return this._lengthList;
    }

    /**
     * Gets the list of the coil counts in the spreadsheet.
     *
     * @return The list of the coil counts in the spreadsheet.
     */
    public ArrayList<Integer> getCoilList() {
        return this._coilList;
    }

    /**
     * Gets the list of the priorities of the coils in the spreadsheet.
     *
     * @return The list of the priorities of the coils in the spreadsheet.
     */
    public ArrayList<Integer> getPriorityList() {
        return this._priorityList;
    }

    /**
     * Gets the map of the setup specifications to their values.
     *
     * @return The map of the setup specifications to their values.
     */
    public HashMap<String, Integer> getSetupSpecs() {
        return this._setupSpecs;
    }

    /**
     * Gets the unique coil lengths
     * 
     * @return The unique coil lengths and their counts
     */
    public HashMap<Integer, Integer> getUniqueCoilLengthsAndAmounts() {
        HashMap<Integer, Integer> uniqueCoilLengths = new HashMap<>();
        for (int i = 0; i < this._lengthList.size(); i++) {
            int length = this._lengthList.get(i);
            int count = this._coilList.get(i);
            if (uniqueCoilLengths.containsKey(length)) {
                uniqueCoilLengths.put(length, uniqueCoilLengths.get(length) + count);
            } else {
                uniqueCoilLengths.put(length, count);
            }
        }
        return uniqueCoilLengths;
    }

}
