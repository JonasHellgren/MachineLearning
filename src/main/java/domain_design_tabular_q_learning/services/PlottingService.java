package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.shared.AgentPlotter;
import domain_design_tabular_q_learning.domain.shared.EnvironmentPlotter;
import domain_design_tabular_q_learning.domain.shared.TrainerPlotter;
import domain_design_tabular_q_learning.domain.trainer.Trainer;

import java.io.IOException;

public class PlottingService {

    EnvironmentPlotter environmentPlotter;
    TrainerPlotter trainerPlotter;
    AgentPlotter agentPlotter;


    public PlottingService(Trainer trainer, Agent agent, EnvironmentRoad environment, PlottingSettings settings) {
        this.trainerPlotter = new TrainerPlotter(trainer);
        this.agentPlotter = new AgentPlotter(agent,environment,settings);
        this.environmentPlotter=new EnvironmentPlotter(environment,settings);
    }

    public static PlottingService ofTrainingService(TrainingService service, PlottingSettings settings) {
        return new PlottingService(service.getTrainer(),service.getAgent(),service.getEnvironment(),settings);
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

    public void saveEnvironmentChart(String dir, String fileName, String fileEnd) throws IOException {
        environmentPlotter.savePlot(dir,fileName,fileEnd);
    }

    public void saveTrainingCharts(String dir, String fileName, String fileEnd) throws IOException {
        trainerPlotter.saveCharts(dir,fileName,fileEnd);
    }

    public void saveAgentCharts(String dir, String fileName, String fileEnd) throws IOException {
        agentPlotter.saveCharts(dir,fileName,fileEnd);
    }
}
