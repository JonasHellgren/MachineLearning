package maze_domain_design.services;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.shared.AgentPlotter;
import maze_domain_design.domain.shared.EnvironmentPlotter;
import maze_domain_design.domain.shared.TrainerPlotter;
import maze_domain_design.domain.trainer.Trainer;

import java.io.IOException;

public class PlottingService {

    EnvironmentPlotter environmentPlotter;
    TrainerPlotter trainerPlotter;
    AgentPlotter agentPlotter;


    public PlottingService(Trainer trainer, Agent agent,Environment environment, PlottingSettings settings) {
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
