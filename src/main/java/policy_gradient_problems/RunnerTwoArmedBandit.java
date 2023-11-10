package policy_gradient_problems;

import policy_gradient_problems.common.TrackingItem;
import policy_gradient_problems.twoArmedBandit.Agent;
import policy_gradient_problems.twoArmedBandit.Environment;
import policy_gradient_problems.twoArmedBandit.Trainer;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static void main(String[] args) {

        var environment=Environment.newWithProbabilities(0.5,1.0);
        Agent agent = Agent.newDefault();
        Trainer trainer = createTrainer(environment,agent);

        trainer.train();

        List<TrackingItem> trackingList=trainer.getTracker().getTrackingItemList();
        trackingList.forEach(System.out::println);




    }


    private static Trainer createTrainer(Environment environment, Agent agent) {
        return Trainer.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(100).nofStepsMax(1).gamma(1d).learningRate(0.2)
                .build();
    }

}
