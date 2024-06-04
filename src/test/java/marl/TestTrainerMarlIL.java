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

    EnvironmentApple environment;
    AppleSettings settings=AppleSettings.newDefault();
    TrainerMarlIL<VariablesApple> trainer;

    Supplier<StateI<VariablesApple>> startStateSupplier;

    @BeforeEach
    void init() {
        environment=new EnvironmentApple(settings);
        List<AgentI<VariablesApple>> agents=List.of(new AgentApple<>(),new AgentApple<>());
        startStateSupplier=new StartStateSupplierFactoryApple(settings).create(POS_APPLE);

        trainer= TrainerMarlIL.<VariablesApple>builder()
                .environment(environment)
                .agents(agents)
                .trainerParameters(TrainerParameters.newDefault().withNofEpisodes(10))
                .startStateSupplier(startStateSupplier)
                .build();
    }

    @Test
    void whenTrained_thenOkCritic() {

        trainer.train();

    }



}
