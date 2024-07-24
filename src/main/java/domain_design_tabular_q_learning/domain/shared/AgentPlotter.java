package domain_design_tabular_q_learning.domain.shared;

import common.other.Conditionals;
import common.plotters.table_shower.TableDataI;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.obstacle_on_road.*;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.services.PlottingSettings;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public abstract class AgentPlotter<V,A> {

    public Agent<V,A> agent;
    public PlottingSettings settings;
    public GridSizeExtractor extractor;


    //inner class to ease data transfer between methods

    protected abstract Tables createTables();

    public void plot() {
       // var nXnY = new GridSizeExtractor(environment.getProperties());
        Tables tables = createTables();
        showTables(tables);
    }


    public void saveCharts(String dir, String fileName, String fileEnd) throws IOException {
      //  var nXnY = new GridSizeExtractor(environment.getProperties());
        Tables tables = createTables();
        var ssMap = getShowerSettingsMap(tables);
        for(Map.Entry<TableShower, TableDataI> e: ssMap.entrySet()) {
            var frame = e.getKey().createTableFrame(e.getValue());
            e.getKey().saveTableFrame(frame, dir, fileName + e.getKey().getSettings().name() + fileEnd);
        }

    }

    void showTables( Tables tables) {
        var ssMap = getShowerSettingsMap(tables);
        ssMap.forEach((key, value) -> SwingUtilities.invokeLater(() -> key.showTable(value)));
    }

    @NotNull
    private Map<TableShower, TableDataI> getShowerSettingsMap(Tables tables) {
        Map<TableShower, TableDataI> showerSettingsMap=new HashMap<>();
        var settingsValues = TableSettings.ofNxNy(extractor.nX(), extractor.nY())
                .withName("State values").withFormatTicks("%.0f");
        var settingsActions = TableSettings.ofNxNy(extractor.nX(), extractor.nY())
                .withName("Actions").withFormatTicks("%.0f");
        var settingsStateActionValues = TableSettings.ofNxNy(extractor.nX(), extractor.nY())
                .withName("State action values").withFormatTicks("%.0f")
                .withMaxCharsPerCell(settings.maxCharsPerStateActionCell());
        var tableDataValues = TableDataString.ofMat(tables.values());
        var tableDataActions = TableDataString.ofMat(tables.actions());
        var tableDataSaValues = TableDataString.ofMat(tables.stateActionValues());
        showerSettingsMap.put(new TableShower(settingsValues),tableDataValues);
        showerSettingsMap.put(new TableShower(settingsActions),tableDataActions);
        showerSettingsMap.put(new TableShower(settingsStateActionValues),tableDataSaValues);
        return showerSettingsMap;
    }


}
