package lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomAndClipped;
import book_rl_explained.lunar_lander.domain.trainer.*;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;

import static lunar.RunnerHelper.*;

@Log
public class RunnerTrainerLunarMultiStep {

    public static final int STEP_HORIZON = 10;
    public static final int N_EPISODES = 5_000;

    public static void main(String[] args) {
        var ep =  LunarProperties.defaultProps();
        var trainerDependencies = getDependencies(ep, STEP_HORIZON, N_EPISODES);
        var trainer = TrainerLunarMultiStep.of(trainerDependencies);
        trainer.train();
        plot(trainer, trainerDependencies);
        evaluate(trainerDependencies, ep);
    }


}
