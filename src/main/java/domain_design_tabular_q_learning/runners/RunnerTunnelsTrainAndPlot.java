package domain_design_tabular_q_learning.runners;

import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadActionProperties;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import domain_design_tabular_q_learning.environments.tunnels.PropertiesTunnels;
import domain_design_tabular_q_learning.environments.tunnels.TunnelActionProperties;
import domain_design_tabular_q_learning.services.*;
import lombok.SneakyThrows;

public class RunnerTunnelsTrainAndPlot {

    static final String DIR=
            "src/main/java/domain_design_tabular_q_learning/documentation/environments/tunnels/pics/";
    public static final String PNG = ".png";

    static TrainingService<XyPos, TunnelActionProperties, PropertiesTunnels> training;
    static TunnelsPlottingService<XyPos,TunnelActionProperties, PropertiesTunnels> plotting;

    @SneakyThrows
    public static void main(String[] args) {
   training = TrainingServiceFactory.createTunnels();
     /*        training.getEnvironment().setProperties(
                training.getEnvironment().getProperties().withRewardFailTerminalStd(30d));
        training.train();
*/
        PlottingSettings settings= PlottingSettings.newRunnerRoad();
        plotting = TunnelsPlottingService.ofTrainingService(training,settings);
        plotting.plotEnvironment();
       // plotting.plotTrainer();
      //  plotting.plotAgent();

        plotting.saveEnvironmentChart(FileDirName.of(DIR,"tunnelsEnv", PNG));
        //plotting.saveTrainingCharts(FileDirName.of(DIR,"training", PNG));
        //plotting.saveAgentCharts(FileDirName.of(DIR,"agent", PNG));*/
    }
}
