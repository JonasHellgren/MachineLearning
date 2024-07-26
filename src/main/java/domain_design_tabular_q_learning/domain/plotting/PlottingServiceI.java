package domain_design_tabular_q_learning.domain.plotting;

import java.io.IOException;

/**
 * Template class for plotting of any environment
 */

public interface PlottingServiceI {
    void plotEnvironment();
    void plotTrainer();
    void plotAgent();
    void saveEnvironmentChart(FileDirName file) throws IOException;
    void saveTrainingCharts(FileDirName file) throws IOException;
    void saveAgentCharts(FileDirName file) throws IOException;
}
