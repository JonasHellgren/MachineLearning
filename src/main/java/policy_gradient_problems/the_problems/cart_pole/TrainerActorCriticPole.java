package policy_gradient_problems.the_problems.cart_pole;

import common.Conditionals;
import common.Counter;
import common_records.NetSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.WeightsDotProductFeatureValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

import static common.Conditionals.executeIfTrue;

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
            agent.setState(StatePole.newAllRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                     super.createExperienceListWithReturns(experienceList, parameters.gamma());

            var experienceListInfo = new NStepReturnInfoPole(experienceList, parameters);
            Counter timeCounter = new Counter();
            int T = experienceList.size();
            List<List<Double>> stateValuesList = new ArrayList<>();
            List<Double> valueTarList = new ArrayList<>();

            //Update critic
            for (ExperiencePole e:experienceList) {
                var resultManySteps = experienceListInfo.getResultManySteps(e);
                double sumRewards = resultManySteps.sumRewards();
                double valueFut= (resultManySteps.stateFuture().isPresent())  //todo other logic
                        ? gammaPowN*valueFunction.getOutValue(resultManySteps.stateFuture().orElseThrow().asList())
                        : 0;
                stateValuesList.add(e.state().asList());
                valueTarList.add(sumRewards + valueFut);
            }
           // System.out.println("ei = " + ei+", T = "+T);
           // experienceList.forEach(System.out::println);
           // valueTarList.forEach(System.out::println);
            int finalEi = ei;
            executeIfTrue(stateValuesList.isEmpty(), () -> log.warning("Empty list, ei = " + finalEi));
            executeIfTrue(!stateValuesList.isEmpty(), () -> valueFunction.fit(stateValuesList, valueTarList));

            //Update actor
            //for (int tau = 0; tau < T - n + 1; tau++) {
            for (int tau = 0; tau < T ; tau++) {

                //Calulate return G and advantage
              //  var resultManySteps = experienceListInfo.getResultManySteps(tau);
                var expAtTau = experienceListInfo.getExperience(tau);
                //double G = resultManySteps.sumRewards();
                double G=experienceListWithReturns.get(tau).value();
                Double value = valueFunction.getOutValue(expAtTau.state().asList());
                double advantage=G-value;

                //Update actor
                var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * advantage);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
                timeCounter.increase();
            }


            updateTracker(ei, List.of((double) experienceList.size()));

            //updateTracker(ei, List.of(valueFunction.getError()));
        }
    }

    /*
    private double getValueRef(NStepReturnInfoPole.ResultManySteps resultsManySteps) {
        if (resultsManySteps.isEndOutside()) {
            return resultsManySteps.sumRewards() + 0d;
        } else {
            ArrayRealVector featureVector =
                    getFeatureVector(resultsManySteps.stateFuture().orElseThrow(), environment.getParameters());
            double valueFutureState = Math.pow(parameters.gamma(), parameters.stepHorizon()) *
                    valueFunction.getValue(featureVector);
            return resultsManySteps.sumRewards() + valueFutureState;
        }
    }

    @NotNull
    public static ArrayRealVector getFeatureVector(StatePole state, ParametersPole p) {
        return new ArrayRealVector(new double[]{
                1d,
                Math.abs(state.angle()) / p.angleMax(),
                Math.abs(state.x()) / p.xMax()
        });
    }  */

}
