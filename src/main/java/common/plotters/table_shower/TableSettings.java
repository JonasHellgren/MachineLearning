package common.plotters.table_shower;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.util.Optional;

@Builder
public record TableSettings(
        @NonNull Integer nX,
        @NonNull Integer nY,
        @With int fontSize,
        @With int maxCharsPerCell,
        @With int padding,
        @With String name,
        @With String fontName,
        @With String formatCell,
        @With String xName,
        @With String yName,
        @With String formatTicks,
        @With Optional<String[]> colNames,
        @With Optional<String[]> rowNames,
        @With double nXstart,
        @With double nXend,
        @With double nYstart,
        @With double nYend,
        @With boolean isScrollPane,
        @With boolean isReverseY
) {

    public static final boolean IF_NO_NAMES_THEN_OK = true;

    public static TableSettings ofNxNy(int nX, int nY) {
        return TableSettings.builder()
                .nX(nX).nY(nY)
                .fontSize(30).fontName("Serif").formatCell("%.2f")
                .maxCharsPerCell(5).padding(20)
                .name("")
                .xName("x").yName("y").formatTicks("%.1f")
                .colNames(Optional.empty()).rowNames(Optional.empty())
                .nXstart(0).nXend(nX)
                .nYstart(0).nYend(nY)
                .isScrollPane(true).isReverseY(true)
                .build();
    }

    public double nXstep() {
      return (nXend-nXstart)/nX;
    }

    public double nYstep() {
        return (nYend-nYstart)/nY;
    }

    public boolean isNofColNamesOk() {
        return  colNames().map(n -> n.length == nX()).orElse(IF_NO_NAMES_THEN_OK);
    }

    public boolean isNofRowNamesOk() {
        return  rowNames().map(n -> n.length == nY()).orElse(IF_NO_NAMES_THEN_OK);
    }

    public boolean isDataOk(TableDataI data0) {
        return data0.nX()==nX && data0.nY()==nY;
    }

}