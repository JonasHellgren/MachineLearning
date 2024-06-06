package marl;

import common.math.Discrete2DPos;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.environments.apple.AppleSettings;
import multi_agent_rl.environments.apple.VariablesObservationApple;
import multi_agent_rl.environments.apple.VariablesStateApple;
import multi_agent_rl.factories.StartStateSupplierFactoryApple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.function.Supplier;

public class TestStartStateSupplierFactoryApple {

    public static final Discrete2DPos POS_APPLE = Discrete2DPos.of(2, 2);

    Supplier<StateI<VariablesStateApple, VariablesObservationApple>> startStateSupplier;
    AppleSettings settings = AppleSettings.newDefault();

    @BeforeEach
    void init() {
        startStateSupplier = new StartStateSupplierFactoryApple(settings).create(POS_APPLE);
    }


    @Test
    void correctStartPos() {
        for (int i = 0; i < 100 ; i++) {
            StateI<VariablesStateApple,VariablesObservationApple> state=startStateSupplier.get();
            Assertions.assertNotEquals(POS_APPLE, state.getVariables().posA());
            Assertions.assertNotEquals(POS_APPLE, state.getVariables().posB());
            Assertions.assertNotEquals(state.getVariables().posA(), state.getVariables().posB());
        }

    }


}
