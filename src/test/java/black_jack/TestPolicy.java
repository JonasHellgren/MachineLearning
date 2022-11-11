package black_jack;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateCards;
import black_jack.models_cards.StateObserved;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.policies.PolicyGreedyOnStateActionMemory;
import black_jack.policies.PolicyInterface;
import black_jack.policies.PolicyRandom;
import org.jcodec.common.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestPolicy {


    private static final int NOF_ACTIONS = 1000;
    private static final double HIGH_PROBABILITY_RANDOM_ACTION = 0.9;

    @Test
    public void testPolicyRandom () {
        PolicyRandom policyRandom=new PolicyRandom();
        List<CardAction> sticks = getSticksList(policyRandom);
        System.out.println("sticks.size() = " + sticks.size());
        Assert.assertTrue(Math.abs(NOF_ACTIONS- sticks.size())<0.6*NOF_ACTIONS);
    }

    @NotNull
    private List<CardAction> getSticksList(PolicyInterface policyRandom) {
        List<CardAction> sticks=new ArrayList<>();
        for (int i = 0; i < NOF_ACTIONS; i++) {
            StateCards cards=StateCards.newRandomPairs();
            if (policyRandom.hitOrStick(cards.observeState()).equals(CardAction.stick)) {
                sticks.add(CardAction.hit);
            }
        }
        return sticks;
    }

    @Test public void testPolicyGreedyOnStateActionMemory() {
        StateActionValueMemory memory = new StateActionValueMemory();
        StateObserved state=new StateObserved(21,true,10);
        memory.write(StateActionObserved.newFromStateAndAction(state,CardAction.stick),1);
        memory.write(StateActionObserved.newFromStateAndAction(state,CardAction.hit),0);

        PolicyGreedyOnStateActionMemory policy=new PolicyGreedyOnStateActionMemory(memory,0);
        CardAction action=policy.hitOrStick(state);
        System.out.println("policy.getActionValueList() = " + policy.getActionValueList());
        policy.getActionValueList().forEach(System.out::println);
        Assert.assertTrue(CardAction.stick.equals(action));
    }

    @Test public void testPolicyRandomGreedyOnStateActionMemory() {
        StateActionValueMemory memory = new StateActionValueMemory();
        StateObserved state=new StateObserved(21,true,10);
        memory.write(StateActionObserved.newFromStateAndAction(state,CardAction.stick),1);
        memory.write(StateActionObserved.newFromStateAndAction(state,CardAction.hit),0);

        PolicyGreedyOnStateActionMemory policy=new PolicyGreedyOnStateActionMemory(memory, HIGH_PROBABILITY_RANDOM_ACTION);

        List<CardAction> sticks = getSticksList(policy);
        System.out.println("sticks.size() = " + sticks.size());

        //despite stick is better selection, action selection is almost random, distribution is
        // therefore fairly even between stick and hit
        Assert.assertTrue(Math.abs(NOF_ACTIONS- sticks.size())<NOF_ACTIONS*0.6);
    }


}
