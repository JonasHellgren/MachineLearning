package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.domain.plotting.RoadEnvironmentPlotter;
import domain_design_tabular_q_learning.domain.plotting.TrainerPlotter;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.environments.obstacle_on_road.RoadAgentPlotter;
import java.io.IOException;

public class RoadPlottingService<V,A> {

    RoadEnvironmentPlotter environmentPlotter;
    TrainerPlotter<V,A> trainerPlotter;
    RoadAgentPlotter<V,A> agentPlotter;

    public RoadPlottingService(Trainer<V,A> trainer,
                               Agent<V,A> agent,
                               EnvironmentI<V,A> environment,
                               PlottingSettings settings) {
        this.trainerPlotter = new TrainerPlotter<>(trainer);
        this.agentPlotter = new RoadAgentPlotter<>(
                agent,
                environment,
                settings);
        this.environmentPlotter=new RoadEnvironmentPlotter<>(environment,settings);
    }

    public static <V,A> RoadPlottingService<V,A> ofTrainingService(TrainingService<V,A> service, PlottingSettings settings) {
        return new RoadPlottingService<V,A>(service.getTrainer(),service.getAgent(),service.getEnvironment(),settings);
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
