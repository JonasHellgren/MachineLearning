package black_jack;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateCards;
import black_jack.models_cards.StateObserved;
import black_jack.policies.PolicyRandom;
import org.jcodec.common.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestPolicy {


    private static final int NOF_ACTIONS = 1000;

    @Test
    public void testPolicyRandom () {

        PolicyRandom policyRandom=new PolicyRandom();

        List<CardAction> sticks=new ArrayList<>();
        List<CardAction> hits=new ArrayList<>();

        for (int i = 0; i < NOF_ACTIONS; i++) {

            StateCards cards=StateCards.newRandomPairs();

            if (policyRandom.hitOrStick(cards.observeState()).equals(CardAction.hit)) {
                sticks.add(CardAction.hit);
            } else {
                hits.add(CardAction.stick);
            }
        }
        System.out.println("sticks.size() = " + sticks.size());
        System.out.println("hits.size() = " + hits.size());

        Assert.assertTrue(Math.abs(sticks.size()- hits.size())<NOF_ACTIONS/4);

    }
}
