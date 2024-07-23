package common.plotters.table_shower;

import lombok.SneakyThrows;
import javax.swing.*;
import java.util.Optional;

public class RunnerShowTable {

    public static final String CHART_DIR = "src/main/java/plotting/saved_charts/";

    @SneakyThrows
    public static void main(String[] args) {
        int nX = 5;
        int nY = 5;

        var settings1 = TableSettings.ofNxNy(nX, nY).withName("Double cells - num ticks");
        var settings2 = TableSettings.ofNxNy(nX, nY)
                .withColNames(Optional.of(new String[]{"A", "B", "C", "D", "E"}))
                .withRowNames(Optional.of(new String[]{"r0", "r1", "r2", "r3", "r4"}))
                .withName("Double cells - string ticks");
        var settings3 = TableSettings.ofNxNy(nX, nY).withName("String cells - num ticks");
        var tableDataDouble = TableDataDouble.ofMatAndSettings(createDoubleTableData(nX, nY), settings1);
        var tableDataString = TableDataString.ofMat(createStringTableData(nX, nY));
        var tableShower1 = new TableShower(settings1);
        var tableShower2 = new TableShower(settings2);
        var tableShower3 = new TableShower(settings3);
        SwingUtilities.invokeLater(() -> tableShower1.showTable(tableDataDouble));
        SwingUtilities.invokeLater(() -> tableShower2.showTable(tableDataDouble));
        SwingUtilities.invokeLater(() -> tableShower3.showTable(tableDataString));

        var frame = tableShower1.createTableFrame(tableDataDouble);
        tableShower1.saveTableFrame(frame, CHART_DIR, "table1.png");
    }

    static Double[][] createDoubleTableData(int nX, int nY) {
        Double[][] data0 = new Double[nX][nY];
        for (int yi = 0; yi < nY; yi++) {
            for (int xi = 0; xi < nX; xi++) {
                data0[xi][yi] = (double) (xi + yi); // Example function
            }
        }
        return data0;
    }

    static String[][] createStringTableData(int nX, int nY) {
        String[][] data0 = new String[nX][nY];
        for (int yi = 0; yi < nY; yi++) {
            for (int xi = 0; xi < nX; xi++) {
                data0[xi][yi] = "-" + (xi + yi) + "-"; // Example function
            }
        }
        return data0;
    }

}
