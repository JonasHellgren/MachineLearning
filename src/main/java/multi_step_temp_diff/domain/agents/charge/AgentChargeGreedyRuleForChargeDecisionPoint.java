package multi_step_temp_diff.domain.agents.charge;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * The logic is that:
 * 1) a vehicle at node after split for not charging with low soc gives rewardBad
 * 2) a vehicle at node after split for charging  with high soc gives rewardBad
 * 3) none of above gives zero value
 *
 */

import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.isAnyAtNode;
import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.socOfAtNode;

public class AgentChargeGreedyRuleForChargeDecisionPoint extends AgentAbstract<ChargeVariables>
        {

    public static final int DISCOUNT_FACTOR = 1;
    public static final double ZERO = 0d;
    public static final int NO_CHARGE_NODE = 11, WILL_CHARGE_NODE = 20;

    double socLimit;
    ChargeEnvironmentSettings settings;

    public AgentChargeGreedyRuleForChargeDecisionPoint(@NonNull EnvironmentInterface<ChargeVariables> environment,
                                                       @NonNull StateInterface<ChargeVariables> state,
                                                       double socLimit,
                                                       ChargeEnvironmentSettings settings,
                                                       AgentChargeNeuralSettings agentSettings) {
        super(environment, state, agentSettings);
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

        Optional<Double> soc11= isAnyAtNode.test(state,NO_CHARGE_NODE)
                ? Optional.of(socOfAtNode.apply(state,NO_CHARGE_NODE))
                : Optional.empty();

        Optional<Double> soc20= isAnyAtNode.test(state,WILL_CHARGE_NODE)
                ? Optional.of(socOfAtNode.apply(state,WILL_CHARGE_NODE))
                : Optional.empty();

        BiConsumer<RewardContainer,Double> rewardNoChargeConsumer= (rc,s) ->
                rc.set((s<socLimit)?settings.rewardBad():ZERO);

        BiConsumer<RewardContainer,Double> rewardWillChargeConsumer= (rc,s) ->
                rc.set((s>socLimit)?settings.rewardBad():ZERO);

        RewardContainer rcNoCharge=new RewardContainer(ZERO);
        soc11.ifPresent(s -> rewardNoChargeConsumer.accept(rcNoCharge,s));

        RewardContainer rcWillCharge=new RewardContainer(ZERO);
        soc20.ifPresent(s -> rewardWillChargeConsumer.accept(rcWillCharge,s));

        return rcNoCharge.reward+ rcWillCharge.reward;
    }

    @Override
    public int chooseAction(double probRandom) {
        return super.chooseAction(0);
    }
}
