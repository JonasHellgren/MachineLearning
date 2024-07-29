package safe_rl.environments.factories;

import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.environments.trading_electricity.SettingsTrading;

public class AgentParametersFactory {

    private AgentParametersFactory() {
    }

    public static AgentParameters buying5Hours() {
        return AgentParameters.newDefault()
                .withTargetMean(2d).withTargetLogStd(Math.log(3d)).withTargetCritic(0d)
                .withAbsActionNominal(2d)
                .withLearningRateActorMean(1e-2)
                .withLearningRateActorStd(1e-3)
                .withLearningRateCritic(1e-1)
                .withGradMaxActor(1d).withGradMaxCritic(1d);
    }

    public static AgentParameters trading24Hours(SettingsTrading settings, Double gradCriticMax) {
        double powerNom = settings.powerBattMax() / 10;
        return  AgentParameters.newDefault()
                .withTargetMean(0d)
                .withTargetLogStd(Math.log(settings.powerBattMax()))
                .withTargetCritic(0d)
                .withAbsActionNominal(powerNom) //1
                .withLearningRateCritic(1e-1)
                .withLearningRateActorMean(1e-2)
                .withLearningRateActorStd(1e-2)
                .withGradMaxActor(1d).withGradMaxCritic(gradCriticMax);
    }


}
