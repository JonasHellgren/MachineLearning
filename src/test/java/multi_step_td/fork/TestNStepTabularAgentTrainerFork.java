package multi_step_td.fork;


import lombok.SneakyThrows;
import multi_step_temp_diff.domain.helpers_specific.ForkHelper;
import multi_step_temp_diff.domain.helpers_specific.ForkTrainerFactory;
import multi_step_temp_diff.domain.helpers_specific.MazeHelper;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.trainer.NStepTabularAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkTabular;
import multi_step_temp_diff.domain.trainer_valueobj.NStepTabularAgentTrainerSettings;
import org.jcodec.common.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class TestNStepTabularAgentTrainerFork {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 2000;
    NStepTabularAgentTrainer<ForkVariables> trainer;
    AgentForkTabular agent;
    ForkEnvironment environment;

    @BeforeEach
    public void init() {
        environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);
    }

    @SneakyThrows
    @Test
    @Tag("tabtrain")
    public void whenIncreasingNofSteps_thenBetterStateValues() {
        NStepTabularAgentTrainerSettings settings = getnStepTabularAgentTrainerSettings(ONE_STEP);
        trainer= createTrainer(environment, settings);

        trainer.train();
        Map<StateInterface<ForkVariables>, Double> mapOneStep= trainer.getStateValueMap();
        ForkHelper helper=new ForkHelper(environment);
        double avgErrOne= helper.avgErrorFork(mapOneStep);

        agent.clear();
        settings = getnStepTabularAgentTrainerSettings(THREE_STEPS);
        trainer= createTrainer(environment, settings);
        trainer.train();
        Map<StateInterface<ForkVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree= helper.avgErrorFork(mapTreeSteps);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);
    }

    private  NStepTabularAgentTrainerSettings getnStepTabularAgentTrainerSettings(int nofStepsBetweenUpdatedAndBackuped) {
        return NStepTabularAgentTrainerSettings.builder()
                .nofEpis(NOF_EPISODES)
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .alpha(0.2).probStart(0.25).probEnd(1e-5).build();
    }

    private  NStepTabularAgentTrainer<ForkVariables> createTrainer(ForkEnvironment environment, NStepTabularAgentTrainerSettings settings) {
        return NStepTabularAgentTrainer.<ForkVariables>builder()
                .settings(settings).environment(environment).agent(agent)
                .startStateSupplier(() -> ForkState.newFromPos(0))
                .build();
    }


}
