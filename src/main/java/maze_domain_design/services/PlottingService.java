package maze_domain_design.services;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.shared.AgentPlotter;
import maze_domain_design.domain.shared.TrainerPlotter;
import maze_domain_design.domain.trainer.Trainer;

public class PlottingService {
    Trainer trainer;
    Agent agent;
    //Environment environment;
    TrainerPlotter trainerPlotter;
    AgentPlotter agentPlotter;

    public PlottingService(Trainer trainer, Agent agent,Environment environment, PlottingSettings settings) {
        this.trainer = trainer;
        this.agent = agent;
        this.trainerPlotter = new TrainerPlotter(trainer);
        this.agentPlotter = new AgentPlotter(agent,environment,settings);
    }

    public static PlottingService ofTrainingService(TrainingService service, PlottingSettings settings) {
        return new PlottingService(service.getTrainer(),service.getAgent(),service.getEnvironment(),settings);
    }

    public void plotTrainer() {
        trainerPlotter.plot();
    }

    public void plotAgent() {
        agentPlotter.plot();
    }
}
