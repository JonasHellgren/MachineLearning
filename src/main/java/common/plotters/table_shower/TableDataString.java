package common.plotters.table_shower;

import com.google.common.base.Preconditions;
import lombok.Builder;

@Builder
public class TableDataString implements TableDataI{
    String[][] stringMat;

    public static TableDataString ofMat(String[][] stringMat) {
        return TableDataString.builder()
                .stringMat(stringMat)
                .build();
    }

    @Override
    public String read(int x, int y) {
        Preconditions.checkArgument(y<stringMat[0].length && x<stringMat.length,
                "Bad x/y");
        return stringMat[x][y];
    }

    @Override
    public int nX() {
        return stringMat.length;
    }

    @Override
    public int nY() {
        return stringMat[0].length;
    }
}
