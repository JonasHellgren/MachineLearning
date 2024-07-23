package common.plotters.table_shower;

import com.google.common.base.Preconditions;
import lombok.Builder;

@Builder
public class TableDataDouble implements TableDataI {
    Double[][] doubleMat;
    TableSettings settings;

    public static TableDataDouble ofMatAndSettings(Double[][] doubleMat,
                                                   TableSettings settings) {
        return TableDataDouble.builder()
                .doubleMat(doubleMat)
                .settings(settings)
                .build();
    }

    @Override
    public String read(int x, int y) {
        Preconditions.checkArgument(y<doubleMat[0].length && x<doubleMat.length,
                "Bad x/y");
        return String.format(settings.formatCell(), doubleMat[x][y]);
    }

    @Override
    public int nX() {
        return doubleMat.length;
    }

    @Override
    public int nY() {
        return doubleMat[0].length;
    }
}
