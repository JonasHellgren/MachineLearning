package safe_rl.runners.buying;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerOneStepACDC;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.factories.TrainerParametersFactory;

/***
 * targetLogStd: very important, good enough init exploration needed
 */

@Log
public class Runner3HoursBuying {
    public static final double SOC_START = 0.2;
    @SneakyThrows
    public static void main(String[] args) {
        var trainer = createTrainer();
        trainer.train();
        trainer.getRecorder().plot("One step ACDC");
        //log.info("agent = " + trainer.getAgent());
    }

    private static TrainerOneStepACDC<VariablesBuying> createTrainer() {
        var settings3 = SettingsBuying.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3));
        var agent= AgentACDCSafe.<VariablesBuying>builder()
                .settings(settings3)
                .parameters(AgentParametersFactory.buying3Hours())
                .state(startState)
                .build();
        return TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(TrainerParametersFactory.buying3Hours())
                .startStateSupplier(() -> startState.copy() )
                .build();
    }

}
