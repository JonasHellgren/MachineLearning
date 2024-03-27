package policy_gradient_problems.helpers;

import common_dl4j.Dl4JUtil;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.abstract_classes.ActorUpdaterI;
import policy_gradient_problems.domain.agent_interfaces.NeuralActor;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.environments.cart_pole.StatePole;

import java.util.ArrayList;
import java.util.List;

public class NeuralActorUpdaterCrossPPOLoss<V> implements ActorUpdaterI<V> {

    @Override
    public void updateActor(MultiStepResults msRes, NeuralActor<V> agent) {
        List<List<Double>> inList=new ArrayList<>();
        List<List<Double>> outList=new ArrayList<>();
        for (int step = 0; step < msRes.tEnd() ; step++) {  //correct? or nofSteps
            inList.add(msRes.stateValuesList().get(step));
            outList.add(createLabel(msRes, step));
        }
        agent.fitActor(inList,outList);
    }

    @NotNull
    public static List<Double> createLabel(MultiStepResults msRes, int i) {
        int actionInt = msRes.actionList().get(i).asInt();
        double adv= msRes.valueTarList().get(i)- msRes.valuePresentList().get(i);
//        List<Double> oneHot = Dl4JUtil.createListWithOneHotWithValue(StatePole.nofActions(), actionInt,adv);
  //      oneHot.set(actionInt, adv);
        double probOld=msRes.probActionList().get(i);
        return List.of((double) actionInt,adv,probOld);
    }

}
