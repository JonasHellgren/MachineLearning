package maze_domain_design.services;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.shared.AgentPlotter;
import maze_domain_design.domain.shared.TrainerPlotter;
import maze_domain_design.domain.trainer.Trainer;

public class PlottingService {
    Trainer trainer;
    Agent agent;
    TrainerPlotter trainerPlotter;
    AgentPlotter agentPlotter;

    public PlottingService(Trainer trainer, Agent agent) {
        this.trainer = trainer;
        this.agent = agent;
        this.trainerPlotter = new TrainerPlotter(trainer);
        this.agentPlotter = new AgentPlotter(agent);
    }

    public void plotTrainer() {
        trainerPlotter.plot();
    }

    public void plotAgent() {
        agentPlotter.plot();
    }
}
