package multi_step_td.maze;

import common.MathUtils;
import    lombok.SneakyThrows;
import multi_step_temp_diff.domain.helpers_specific.MazeHelper;
import multi_step_temp_diff.domain.agents.maze.AgentMazeTabular;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.trainer.NStepTabularAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.trainer_valueobj.NStepTabularAgentTrainerSettings;
import org.jcodec.common.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class TestNStepTabularAgentTrainerMaze {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 5;
    private static final int NOF_EPISODES = 100;

    NStepTabularAgentTrainer<MazeVariables> trainer;
    AgentMazeTabular agent;
    MazeEnvironment environment;

    @BeforeEach
    public void init() {
        environment = new MazeEnvironment();
        agent = AgentMazeTabular.newDefault(environment);
    }

    @SneakyThrows
    @Test
    @Tag("tabtrain")
    public void whenStartingAtX0Y5_thenGoodStateValuesAtUpperRow() {
        Supplier<StateInterface<MazeVariables>> startStateSup=() -> MazeState.newFromXY(0,5);
        NStepTabularAgentTrainerSettings settings = getnStepTabularAgentTrainerSettings(ONE_STEP);
        trainer= createTrainer(environment, settings,startStateSup);
        trainer.setStartStateSupplier(() -> MazeState.newFromXY(0,5));
        agent.clear();
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneStep= trainer.getStateValueMap();
        printStateValues(MazeHelper.STATES_MAZE_UPPER,mapOneStep );
        System.out.println("---------------------------");

        double avgErrOne= MazeHelper.avgErrorMaze(mapOneStep, MazeHelper.STATES_MAZE_UPPER);
        agent.clear();

        settings = getnStepTabularAgentTrainerSettings(THREE_STEPS);
        trainer= createTrainer(environment, settings,startStateSup);
        trainer.setStartStateSupplier(() -> MazeState.newFromXY(0,5));
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        printStateValues(MazeHelper.STATES_MAZE_UPPER,mapTreeSteps );

        double avgErrThree= MazeHelper.avgErrorMaze(mapTreeSteps, MazeHelper.STATES_MAZE_UPPER);
        System.out.println("mapTreeSteps = " + mapTreeSteps);
        printErrors(avgErrOne, avgErrThree);
        Assert.assertTrue(avgErrOne>avgErrThree || MathUtils.isEqualDoubles(avgErrOne,avgErrThree,0.001));
    }


    @Test
    @Tag("tabtrain")
    public void whenStartingAtRandom_thenGoodStateValuesAtAllCells() {
        Supplier<StateInterface<MazeVariables>> startStateSup=MazeState::newFromRandomPos;

        agent.clear();
        NStepTabularAgentTrainerSettings settings = getnStepTabularAgentTrainerSettings(ONE_STEP);
        trainer= createTrainer(environment, settings,startStateSup);

        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneSteps= trainer.getStateValueMap();
        printManyStateValues(mapOneSteps);
        double avgErrOne= MazeHelper.avgErrorMaze(mapOneSteps, MazeHelper.STATES_MAZE_MERGED);
        System.out.println("---------------------------");

        agent.clear();
        settings = getnStepTabularAgentTrainerSettings(THREE_STEPS);
        trainer= createTrainer(environment, settings,startStateSup);
        trainer.setStartStateSupplier(MazeState::newFromRandomPos);


        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        printManyStateValues(mapTreeSteps);
        double avgErrThree= MazeHelper.avgErrorMaze(mapTreeSteps, MazeHelper.STATES_MAZE_MERGED);

        printErrors(avgErrOne, avgErrThree);
        Assert.assertTrue(avgErrOne>avgErrThree);

    }

    private void printManyStateValues(Map<StateInterface<MazeVariables>, Double> mapTreeSteps) {
        printStateValues(MazeHelper.STATES_MAZE_UPPER, mapTreeSteps);
        printStateValues(MazeHelper.STATES_MAZE_MIDDLE, mapTreeSteps);
        printStateValues(MazeHelper.STATES_MAZE_BOTTOM, mapTreeSteps);
    }


    private static void printErrors(double avgErrOne, double avgErrThree) {
        System.out.println("avgErrOne = " + avgErrOne +", avgErrThree = " + avgErrThree);
    }

    public void printStateValues(List<MazeState> states, Map<StateInterface<MazeVariables>, Double> stateMap ) {
        DecimalFormat formatter = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        for (MazeState state:states) {
            Double value = stateMap.get(state);
            System.out.println("State = "+state+", value = "+ formatter.format(value));
        }
    }

    private  NStepTabularAgentTrainerSettings getnStepTabularAgentTrainerSettings(int nofStepsBetweenUpdatedAndBackuped) {
        return NStepTabularAgentTrainerSettings.builder()
                .nofEpis(NOF_EPISODES)
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .alpha(0.5).probStart(0.9).probEnd(1e-5).build();
    }

    private  NStepTabularAgentTrainer<MazeVariables> createTrainer(MazeEnvironment environment,
                                                                   NStepTabularAgentTrainerSettings settings,
                                                                   Supplier<StateInterface<MazeVariables>> startStateSup) {
        return NStepTabularAgentTrainer.<MazeVariables>builder()
                .settings(settings).environment(environment).agent(agent)
                .startStateSupplier(startStateSup)
                .build();
    }



}
