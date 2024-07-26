package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadEnvironmentPlotter;
import domain_design_tabular_q_learning.domain.plotting.TrainerPlotter;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadAgentPlotter;
import java.io.IOException;

public class RoadPlottingService<V,A,P> {

    RoadEnvironmentPlotter<V,A,P> environmentPlotter;
    TrainerPlotter<V,A,P> trainerPlotter;
    RoadAgentPlotter<V,A,P> agentPlotter;

    public RoadPlottingService(Trainer<V,A,P> trainer,
                               Agent<V,A,P> agent,
                               EnvironmentI<V,A,P> environment,
                               PlottingSettings settings) {
        this.trainerPlotter = new TrainerPlotter<>(trainer);
        this.agentPlotter = new RoadAgentPlotter<>(
                agent,
                environment,
                settings);
        this.environmentPlotter=new RoadEnvironmentPlotter<>(environment,settings);
    }

    public static <V,A,P> RoadPlottingService<V,A,P> ofTrainingService(TrainingService<V,A,P> service, PlottingSettings settings) {
        return new RoadPlottingService<>(service.getTrainer(), service.getAgent(), service.getEnvironment(), settings);
    }

    public void plotEnvironment() {
        environmentPlotter.plot();
    }

    public void plotTrainer() {
        trainerPlotter.plot();
    }

    public void plotAgent() {
        agentPlotter.plot();
    }

    public void saveEnvironmentChart(FileDirName file) throws IOException {
        environmentPlotter.savePlot(file);
    }

    public void saveTrainingCharts(FileDirName file) throws IOException {
        trainerPlotter.saveCharts(file);
    }

    public void saveAgentCharts(FileDirName file) throws IOException {
        agentPlotter.saveCharts(file);
    }
}
