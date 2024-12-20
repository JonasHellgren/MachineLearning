package domain_design_tabular_q_learning.services;

import lombok.Builder;
import lombok.With;

@Builder
public record PlottingSettings(
        @With  String tableCellFormatValues,
        @With String tableCellFormatActionValues,
        @With Integer maxCharsPerStateActionCell
) {

    public static PlottingSettings newRunnerRoad() {
        return PlottingSettings.builder()
                .tableCellFormatValues("%.1f")
                .tableCellFormatActionValues("%.0f")
                .maxCharsPerStateActionCell(10)
                .build();
    }
}
