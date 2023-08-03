package multi_step_temp_diff.domain.agents.charge;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.Optional;
import java.util.function.BiConsumer;

import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.isAnyAtNode;
import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.socOfAtNode;

public class AgentChargeGreedyRuleForChargeDecisionPoint extends AgentAbstract<ChargeVariables>
        implements AgentInterface<ChargeVariables> {

    public static final int DISCOUNT_FACTOR = 1;
    public static final double ZERO = 0d;
    public static final int NO_CHARGE_NODE = 11, WILL_CHARGE_NODE = 20;

    double socLimit;
    ChargeEnvironmentSettings settings;

    public AgentChargeGreedyRuleForChargeDecisionPoint(@NonNull EnvironmentInterface<ChargeVariables> environment,
                                                       @NonNull StateInterface<ChargeVariables> state,
                                                       double socLimit,
                                                       ChargeEnvironmentSettings settings) {
        super(environment, state, DISCOUNT_FACTOR);
        this.socLimit=socLimit;
        this.settings=settings;
    }

    @AllArgsConstructor
    private class RewardContainer {
        double reward;
        void set(double reward) {
            this.reward=reward;
        }

    }

    @Override
    public double readValue(StateInterface<ChargeVariables> state) {

        Optional<Double> soc11= isAnyAtNode.test(state, NO_CHARGE_NODE)
                ? Optional.of(socOfAtNode.apply(state,NO_CHARGE_NODE))
                : Optional.empty();

        Optional<Double> soc20= isAnyAtNode.test(state, WILL_CHARGE_NODE)
                ? Optional.of(socOfAtNode.apply(state,WILL_CHARGE_NODE))
                : Optional.empty();

        BiConsumer<RewardContainer,Double> rewardNoChargeConsumer= (rc,s) ->
                rc.set((s<socLimit)?settings.rewardBad():ZERO);

        BiConsumer<RewardContainer,Double> rewardWillChargeConsumer= (rc,s) ->
                rc.set((s>socLimit)?settings.rewardBad():ZERO);

        RewardContainer rc11=new RewardContainer(ZERO);
        soc11.ifPresent(s -> rewardNoChargeConsumer.accept(rc11,s));

        RewardContainer rc20=new RewardContainer(ZERO);
        soc20.ifPresent(s -> rewardWillChargeConsumer.accept(rc20,s));

        return rc11.reward+ rc20.reward;

                /*
        boolean isAt11AndLowSoC=soc11.isPresent() && (soc11.get()<socLimit);
        boolean isAt20AndHighSoC=soc20.isPresent() && (soc20.get()>socLimit);
        if (isAt11AndLowSoC) {
            return settings.rewardBad();
        }
        if (isAt20AndHighSoC) {
            return settings.rewardBad();
        }
        return 0;
        */
    }

    @Override
    public int chooseAction(double probRandom) {
        return super.chooseAction(0);
    }
}
