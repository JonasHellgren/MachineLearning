package safe_rl.environments.factories;

import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

public class TrainerSimulatorFactoryTrading {

    public static <V> Pair<TrainerMultiStepACDC<V>, AgentSimulator<V>> createTrainerAndSimulator(
            TrainerParameters trainerParameters,
            AgentParameters parameters,
            SettingsTrading settingsTrading) {
        EnvironmentI<V> environment = (EnvironmentI<V>) new EnvironmentTrading(settingsTrading);
        StateI<V> startState = (StateI<V>) StateTrading.of(VariablesTrading.newSoc(settingsTrading.socStart()));
        SafetyLayer<V> safetyLayer = (SafetyLayer<V>) new SafetyLayer<>(FactoryOptModel.createTradeModel(settingsTrading));

        AgentACDiscoI<V> agent = AgentACDCSafe.of(
                parameters,
                settingsTrading,
                startState.copy());
        var trainer = TrainerMultiStepACDC.<V>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator = AgentSimulator.<V>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }

    public static Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>>
    trainerSimulatorPairNight(SettingsTrading settings, int nofEpisodes) {
        return createTrainerAndSimulator(
                TrainerParametersFactory.tradingNightHours(nofEpisodes),
                AgentParametersFactory.trading24Hours(settings), settings);
    }


}
