package safe_rl.other.scenerio_table;

import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Set;

import static safe_rl.persistance.ElDataFinals.formatter;

public class ScenarioTableHelper {

    public static final int SCEN_NAME_COL = 0;
    public static final int  HEADER_ROW=0;
    public static final int ROW_KEY_G2V = 1;
    public static final int ROW_KEY_V2G = 2;
    public static final Set<Integer> ROWS_SCEANRIOS = Set.of(1, 2);
    public static final Set<Integer> COLUMNS_DATA = Set.of(1, 2, 3);
    public static final int SUM_COLUMN = 4;


    public static void createHeader(Table<Integer, Integer, String> resTable) {
        resTable.put(HEADER_ROW, SCEN_NAME_COL, "Scenario");
        resTable.put(HEADER_ROW, 1, "Trading rev.");
        resTable.put(HEADER_ROW, 2, "HW cost");
        resTable.put(HEADER_ROW, 3, "House el cost");
        resTable.put(HEADER_ROW, 4, "Adj. rev.");
    }

    public static void putDataInRow(Table<Integer, Integer, String> resTable, int rowIdx, String scenName, Triple<Double,Double,Double> data) {
        resTable.put(rowIdx, 0, scenName);
        resTable.put(rowIdx, 1, formatter.format(data.getLeft()));
        resTable.put(rowIdx, 2, formatter.format(data.getMiddle()));
        resTable.put(rowIdx, 3, formatter.format(data.getRight()));
    }

    public static void computeSumColumns(Table<Integer, Integer, String> table, Set<Integer> rowKeys, Set<Integer> columnKeys, int sumColumnKey) {

        for (Integer rowKey : rowKeys) {
            double sum = HEADER_ROW;
            for (Integer columnKey : columnKeys) {
                String value = table.get(rowKey, columnKey);
                if (value != null) {
                    double valueNum = Double.parseDouble(value);
                    sum += valueNum;
                }
            }
            table.put(rowKey, sumColumnKey, Double.toString(sum)); // Put the sum in the specified sum column
        }
    }

    public static <R, C, V> void printTableAsMatrix(Table<R, C, V> table) {
        // Get the row and column keys
        Set<R> rowKeys = table.rowKeySet();
        Set<C> columnKeys = table.columnKeySet();

        // Calculate the maximum length of entries in each column
        int[] maxLengths = new int[columnKeys.size()];
        int i = HEADER_ROW;
        for (C columnKey : columnKeys) {
            maxLengths[i] = columnKey.toString().length();
            for (R rowKey : rowKeys) {
                V value = table.get(rowKey, columnKey);
                if (value != null) {
                    maxLengths[i] = Math.max(maxLengths[i], value.toString().length());
                }
            }
            i++;
        }

        // Print the header row
        System.out.print(String.format("%-8s", "")); // Initial space for the row headers
        i = HEADER_ROW;
        for (C columnKey : columnKeys) {
            System.out.print(String.format("%-" + (maxLengths[i] + 4) + "s", columnKey));
            i++;
        }
        System.out.println();

        // Print the rows
        for (R rowKey : rowKeys) {
            System.out.print(String.format("%-8s", rowKey)); // Print the row header
            i = HEADER_ROW;
            for (C columnKey : columnKeys) {
                V value = table.get(rowKey, columnKey);
                System.out.print(String.format("%-" + (maxLengths[i] + 4) + "s", (value != null ? value : "")));
                i++;
            }
            System.out.println();
        }
    }
}
