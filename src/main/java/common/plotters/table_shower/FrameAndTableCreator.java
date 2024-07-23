package common.plotters.table_shower;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@AllArgsConstructor
public class FrameAndTableCreator {
    public static final int N_COLS_ROW_NAMES = 1;

    TableSettings settings;

    JFrame createFrame(JTable table) {
        JFrame frame = new JFrame(settings.name());
        int frameHeight = getFrameHeight(table);
        int frameWidth = getFrameWidth(table);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (settings.isScrollPane()) {
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);
        } else {
            frame.add(table);
        }
        frame.setSize(frameWidth, frameHeight);
        return frame;
    }

    int getFrameWidth(JTable table) {
        int charWidth = table.getFontMetrics(table.getFont()).charWidth('W');
        int columnWidth = settings.maxCharsPerCell() * charWidth;
        int tableWidth = settings.nX() * columnWidth;
        return tableWidth + settings.padding();
    }

    int getFrameHeight(JTable table) {
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int rowHeight = getHeight(table);
        int tableHeight = headerHeight + ((settings.nY() + 1) * rowHeight);
        return tableHeight + settings.padding();
    }

    JTable createTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        Font font = new Font(settings.fontName(), Font.PLAIN, settings.fontSize());
        table.setFont(font);
        table.getTableHeader().setFont(font);
        int rowHeight = getHeight(table);
        table.setRowHeight(rowHeight);
        return table;
    }


    // isReverseY()=true => reverse y order, data[yi][..]=..[nY-yi-1] => y min in bottom

    Object[][] createTableData(TableDataI data0, String[] rowNames) {
        Preconditions.checkArgument(settings.isDataOk(data0),
                "nX/ny not equal to nof cols/rows in data");
        Object[][] data = new Object[settings.nY()][settings.nX() + N_COLS_ROW_NAMES];
        for (int yi = 0; yi < settings.nY(); yi++) {
            int y0i = settings.isReverseY() ? settings.nY() - yi - 1:yi;
            data[yi][0] = rowNames[y0i];
            for (int xi = N_COLS_ROW_NAMES; xi <= settings.nX(); xi++) {
                int x0i = xi - N_COLS_ROW_NAMES;
                data[yi][xi] = data0.read(x0i,y0i);
            }
        }
        return data;
    }

    int getHeight(JTable table) {
        return table.getFontMetrics(table.getFont()).getHeight();
    }


}
