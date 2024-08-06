package safe_rl.environments.trading_electricity;

import common.other.Conditionals;
import common.other.NormDistributionSampler;
import lombok.extern.java.Log;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.StepReturn;

import java.util.Arrays;

@Log
public class EnvironmentTrading implements EnvironmentI<VariablesTrading> {
    SettingsTrading settings;
    NormDistributionSampler sampler;
    TradeStateUpdater stateUpdater;
    RewardAndConstraintEvaluator evaluator;

    public EnvironmentTrading(SettingsTrading settings) {
        this.settings = settings;
        this.sampler=new NormDistributionSampler();
        this.stateUpdater=new TradeStateUpdater(settings);
        this.evaluator=new RewardAndConstraintEvaluator(settings);
    }

    @Override
    public StepReturn<VariablesTrading> step(StateI<VariablesTrading> state0, Action action) {
        var s0=(StateTrading) state0;
        double power=action.asDouble();
        double aFcrLumped=sampler.sampleFromNormDistribution(0, settings.stdActivationFCR());
        var updaterRes=stateUpdater.update((StateTrading) state0,aFcrLumped,power);
        double reward = evaluator.calculateReward(s0, power, updaterRes.dSoh());
        var constraints=evaluator.evaluateConstraints(s0,power, updaterRes.stateNew());
        boolean isTerminal = stateUpdater.isTerminal(updaterRes.stateNew());
        boolean isFail = evaluator.isAnyConstraintViolated(constraints);
        reward = evaluator.maybeAddFailPenaltyToReward(reward, isFail);
        logIfFail(constraints, isFail,s0);

        return StepReturn.<VariablesTrading>builder()
                .state(updaterRes.stateNew())
                .reward(reward)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .build();
    }

    private static void logIfFail(double[] constraints, boolean isFail, StateTrading s) {
        Conditionals.executeIfTrue(isFail, () ->
                log.info("Failed step, constraints="+ Arrays.toString(constraints)+
                        ", time="+s.variables.time()));
    }




}
