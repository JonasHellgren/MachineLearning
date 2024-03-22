package policy_gradient_problems.helpers;

import common_dl4j.Dl4JUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.agent_interfaces.NeuralActor;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.environments.cart_pole.StatePole;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class NeuralActorUpdater<V> {

    @NonNull NeuralActor<V> agent;

    public void updateActor(MultiStepResults msRes) {
        List<List<Double>> inList=new ArrayList<>();
        List<List<Double>> outList=new ArrayList<>();
        for (int step = 0; step < msRes.tEnd() ; step++) {  //correct? or nofSteps
            inList.add(msRes.stateValuesList().get(step));
            outList.add(createOneHot(msRes, step));
        }
        agent.fitActor(inList,outList);
    }

    @NotNull
    public static List<Double> createOneHot(MultiStepResults msRes, int i) {
        int actionInt = msRes.actionList().get(i).asInt();
        double adv= msRes.valueTarList().get(i)- msRes.valuePresentList().get(i);
        List<Double> oneHot = Dl4JUtil.createListWithOneHotWithValue(StatePole.nofActions(), actionInt,adv);
        oneHot.set(actionInt, adv);
        return oneHot;
    }

}
