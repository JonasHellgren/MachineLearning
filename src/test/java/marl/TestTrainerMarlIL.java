package marl;

import common.math.Discrete2DPos;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.trainers.TrainerMarlIL;
import multi_agent_rl.domain.value_classes.TrainerParameters;
import multi_agent_rl.environments.apple.*;
import multi_agent_rl.factories.StartStateSupplierFactoryApple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

public class TestTrainerMarlIL {

    public static final Discrete2DPos POS_APPLE = Discrete2DPos.of(2, 2);
    public static final Discrete2DPos POS_FAR = Discrete2DPos.of(0, 0);
    public static final Discrete2DPos POS_CLOSE = Discrete2DPos.of(1, 2);
    public static final TrainerParameters TRAINER_PARAMETERS = TrainerParameters.newDefault()
            .withNofEpisodes(10).withStepHorizon(3);

    EnvironmentApple environment;
    AppleSettings settings=AppleSettings.newDefault();
    TrainerMarlIL<VariablesStateApple,VariablesObservationApple> trainer;

    Supplier<StateI<VariablesStateApple,VariablesObservationApple>> startStateSupplier;
    List<AgentI<VariablesObservationApple>> agents;

    @BeforeEach
    void init() {
        environment=new EnvironmentApple(settings);
        agents=List.of(new AgentILApple<>("A"),new AgentILApple<>("B"));
        startStateSupplier=new StartStateSupplierFactoryApple(settings).create(POS_APPLE);

        trainer= TrainerMarlIL.<VariablesStateApple,VariablesObservationApple>builder()
                .environment(environment)
                .agents(agents)
                .trainerParameters(TRAINER_PARAMETERS)
                .startStateSupplier(startStateSupplier)
                .build();
    }

    @Test
    void whenTrained_thenOkCritic() {

        trainer.train();

        StateApple stateClose= StateApple.of(POS_APPLE, POS_CLOSE, POS_CLOSE, settings);
        StateApple stateFar= StateApple.of(POS_APPLE, POS_FAR, POS_FAR, settings);

        double valueClose=agents.get(0).criticOut(stateClose.getObservation("A"));
        double valueFar=agents.get(0).criticOut(stateFar.getObservation("A"));

    }



}
