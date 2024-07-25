package domain_design_tabular_q_learning.domain.plotting;

import common.plotters.table_shower.TableDataI;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.services.PlottingSettings;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Agent2dMemPlotterHelper<V, A> {

    public final Agent<V, A> agent;
    public final PlottingSettings settings;
    GridSizeInformer gridInfo;


    public void saveCharts(Tables tables, FileDirName file) throws IOException {
        var list = getListWithShowerSettingsPairs(tables);
        for (Pair<TableShower, TableDataI> p : list) {
            var shower = p.getFirst();
            var tableData = p.getSecond();
            var frame = shower.createTableFrame(tableData);
            shower.saveTableFrame(
                    frame,
                    file.dir(),
                    file.fileName() + shower.getSettings().name() + file.fileEnd());
        }

    }

    public void showTables(Tables tables) {
        var ssMap = getListWithShowerSettingsPairs(tables);
        ssMap.forEach(p -> {
            var shower = p.getFirst();
            var tableData = p.getSecond();
            SwingUtilities.invokeLater(() -> shower.showTable(tableData));
        });
    }

    @NotNull
    private List<Pair<TableShower, TableDataI>> getListWithShowerSettingsPairs(Tables tables) {
        List<Pair<TableShower, TableDataI>> listOfPairs = new ArrayList<>();
        var settingsValues = TableSettings.ofNxNy(gridInfo.nX(), gridInfo.nY())
                .withName("State values").withFormatTicks("%.0f");
        var settingsActions = TableSettings.ofNxNy(gridInfo.nX(), gridInfo.nY())
                .withName("Actions").withFormatTicks("%.0f");
        var settingsStateActionValues = TableSettings.ofNxNy(gridInfo.nX(), gridInfo.nY())
                .withName("State action values").withFormatTicks("%.0f")
                .withMaxCharsPerCell(settings.maxCharsPerStateActionCell());
        var tableDataValues = TableDataString.ofMat(tables.values());
        var tableDataActions = TableDataString.ofMat(tables.actions());
        var tableDataSaValues = TableDataString.ofMat(tables.stateActionValues());
        listOfPairs.add(Pair.create(new TableShower(settingsValues), tableDataValues));
        listOfPairs.add(Pair.create(new TableShower(settingsActions), tableDataActions));
        listOfPairs.add(Pair.create(new TableShower(settingsStateActionValues), tableDataSaValues));
        return listOfPairs;
    }
}
