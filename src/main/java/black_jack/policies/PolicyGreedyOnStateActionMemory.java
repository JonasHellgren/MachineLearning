package black_jack.policies;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateObserved;
import black_jack.models_memory.StateActionValueMemory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Getter
public class PolicyGreedyOnStateActionMemory implements PolicyInterface {

    static final CardAction ACTION_IF_NO_RESULTS_DEFINED=CardAction.stick;
    static final int REWARD_IF_NO_RESULT_DEFINED = 0;

    @AllArgsConstructor
    @ToString
    @Getter
    public static
    class ActionValue {
        public CardAction action;
        public double value;
    }

    StateActionValueMemory stateActionValueMemory;  //reference
    double probabilityRandomAction;
    List<ActionValue> actionValueList;
    Random random;
    PolicyRandom policyRandom;

    public PolicyGreedyOnStateActionMemory(StateActionValueMemory stateActionValueMemory, double probabilityRandomAction) {
        this.stateActionValueMemory = stateActionValueMemory;
        this.probabilityRandomAction=probabilityRandomAction;
        actionValueList=new ArrayList<>();
        random=new Random();
        policyRandom=new PolicyRandom();
    }

    @Override
    public CardAction hitOrStick(StateObserved state) {
        return (random.nextDouble()<probabilityRandomAction)
                ? policyRandom.hitOrStick(state)
                : hitOrStickNonRandom(state);

    }


    public CardAction hitOrStickNonRandom(StateObserved observed) {
        actionValueList.clear();
        for (CardAction action:CardAction.values()) {
            double value = stateActionValueMemory.read(new StateActionObserved(observed, action));
            actionValueList.add(new ActionValue(action,value));
        }

        ActionValue actionValue= getBestResult(actionValueList);
        return actionValue.action;
    }

    static ActionValue getBestResult(List<ActionValue> actionValueList) {
        Optional<ActionValue> maxReward = actionValueList.stream().
                reduce((res, item) -> res.getValue() > item.getValue() ? res : item);

        return maxReward.orElse(new ActionValue(
                ACTION_IF_NO_RESULTS_DEFINED, REWARD_IF_NO_RESULT_DEFINED));
    }

}
