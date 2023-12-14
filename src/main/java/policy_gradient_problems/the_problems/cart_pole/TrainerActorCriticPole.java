package policy_gradient_problems.the_problems.cart_pole;

import common.Counter;
import common_records.NetSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static common.Conditionals.executeIfTrue;

/***
 *  Details in
 *  https://medium.com/intro-to-artificial-intelligence/the-actor-critic-reinforcement-learning-algorithm-c8095a655c14
 *
 *  An episode gives a set of experiences: e0, e1, e2, ei,.....e_i+n
 *  Two cases are present for ei: 1) it is fail state 2) not fail state
 *  In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 *  But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 *  "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 *  episode. Many environments stop after a specific amount of steps.
 *
 */

@Log
@Getter
public class TrainerActorCriticPole extends TrainerAbstractPole {

    public static final int NOF_FEATURES = 3;
    NeuralMemoryPole valueFunction;
    //WeightsDotProductFeatureValueFunction valueFunction;

    @Builder
    public TrainerActorCriticPole(@NonNull EnvironmentPole environment,
                                  @NonNull AgentPole agent,
                                  @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        NetSettings netSettings = NetSettings.builder().nofFitsPerEpoch(1).learningRate(parameters.beta()).build();
        this.valueFunction = new NeuralMemoryPole(netSettings, environment.getParameters());
    }

    public void train() {
        Integer n = parameters.stepHorizon();
        double gammaPowN = Math.pow(parameters.gamma(), parameters.stepHorizon());
        System.out.println("gammaPowN = " + gammaPowN);

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    super.createExperienceListWithReturns(experienceList, parameters.gamma());

            var experienceListInfo = new NStepReturnInfoPole(experienceList, parameters);
            Counter timeCounter = new Counter();
            int T = experienceList.size();
            List<List<Double>> stateValuesList = new ArrayList<>();
            List<Double> valueTarList = new ArrayList<>();

            //Update critic
            //for (ExperiencePole e : experienceList) {
            int tEnd= experienceListInfo.isEndExperienceFail() ? T : T - n + 1;  //explaind in top of file
            for (int t = 0; t < tEnd; t++) {
                var resultManySteps = experienceListInfo.getResultManySteps(t);
                double sumRewards = resultManySteps.sumRewards();
                double valueFut = (resultManySteps.stateFuture().isPresent())  //todo other logic
                        ? gammaPowN * valueFunction.getOutValue(resultManySteps.stateFuture().orElseThrow().asList())
                        : 0;
                stateValuesList.add(experienceListInfo.getExperience(t).state().asList());
                valueTarList.add(sumRewards + valueFut);
            }
            // System.out.println("ei = " + ei+", T = "+T);
            // experienceList.forEach(System.out::println);
            // valueTarList.forEach(System.out::println);
            int finalEi = ei;
            executeIfTrue(stateValuesList.isEmpty(), () -> log.warning("Empty list, ei = " + finalEi));
            executeIfTrue(!stateValuesList.isEmpty(), () -> valueFunction.fit(stateValuesList, valueTarList));

            //Update actor
            for (int tau = 0; tau < T; tau++) {

                //Calulate return G and advantage
                //  var resultManySteps = experienceListInfo.getResultManySteps(tau);
                var expAtTau = experienceListInfo.getExperience(tau);
                //double G = resultManySteps.sumRewards();
                double r = experienceListWithReturns.get(tau).reward();
                Double valueS = valueFunction.getOutValue(expAtTau.state().asList());
                Double valueSnew = valueFunction.getOutValue(expAtTau.stateNext().asList());
                double advantage = r+valueSnew - valueS;

                //Update actor
                var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * advantage);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
                timeCounter.increase();
            }

            int finalEi1 = ei;
            executeIfTrue(!experienceListInfo.isEndExperienceFail(), () ->
                    log.info("Episode successful, ei = "+ finalEi1+", n steps = "+experienceList.size()));


            updateTracker(ei, List.of((double) experienceList.size()));

            //updateTracker(ei, List.of(valueFunction.getError()));
        }
    }

}
