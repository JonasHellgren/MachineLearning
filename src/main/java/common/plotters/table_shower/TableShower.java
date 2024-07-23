package common.plotters.table_shower;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.io.IOException;

@AllArgsConstructor
public class TableShower {

    @Getter
    TableSettings settings;
    XYAxisTicksCreator xyAxisTicks;
    FrameAndTableCreator creator;
    TableFrameSaver saver;

    public TableShower(TableSettings settings) {
        this.settings = settings;
        this.xyAxisTicks =new XYAxisTicksCreator(settings);
        this.creator=new FrameAndTableCreator(settings);
        this.saver=new TableFrameSaver();
    }

    public void showTable(TableDataI data0) {
        JFrame frame = createTableFrame(data0);
        frame.setVisible(true);
    }

    public JFrame createTableFrame(TableDataI data0) {
        String[] columnNames = xyAxisTicks.columnNames();
        String[] rowNames = xyAxisTicks.createRowNames();
        Object[][] data = creator.createTableData(data0, rowNames);
        var table = creator.createTable(data, columnNames);
        return creator.createFrame(table);
    }

    public void saveTableFrame(JFrame frame,
                               String chartDir,
                               String fileName) throws IOException {
        saver.saveTableFrame(frame,chartDir,fileName);
    }




}
