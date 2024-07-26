package domain_design_tabular_q_learning.domain.plotting;

import java.io.IOException;

public interface Environment2dPlotterI {

    void plot();
    void savePlot(FileDirName file) throws IOException;
}
