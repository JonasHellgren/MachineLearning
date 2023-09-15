package policy_gradient_upOrDown.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Triple;
import policy_gradient_upOrDown.helpers.ReturnCalculator;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
public class Trainer {

    Environment environment;
    Agent agent;
    @NonNull Integer nofEpisodes;
    @NonNull Integer nofStepsMax;
    @NonNull Double gamma, learningRate;

    public void train() {
        ReturnCalculator returnCalculator=new ReturnCalculator();

        List<Triple<Integer,Double,Double>> tripleList = new ArrayList<>();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            List<Double> rewards=new ArrayList<>();
            List<Integer> actions=new ArrayList<>();
            for (int si = 0; si < nofStepsMax ; si++) {
                int action=agent.chooseAction();
                double reward=environment.step(action);
                actions.add(action);
                rewards.add(reward);
            }

            List<Double> returns =  returnCalculator.calcReturns(rewards,gamma);

            for (Integer action:actions) {
                double derGradLog = agent.derGradLogPolicy(action);
                Double v = returns.get(actions.indexOf(action));
                double theta=agent.theta;
                theta+=learningRate*v*derGradLog;
                agent.setTheta(theta);

                Triple<Integer,Double,Double> avd = Triple.of(action,v,derGradLog);
                tripleList.add(avd);
            }
        }
        //tripleList.forEach(System.out::println);
    }
}
