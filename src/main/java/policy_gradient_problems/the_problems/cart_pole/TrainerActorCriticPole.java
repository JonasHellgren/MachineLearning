package policy_gradient_problems.the_problems.cart_pole;

import common.Counter;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.WeightsDotProductFeatureValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

@Log
public class TrainerActorCriticPole extends TrainerAbstractPole {

    public static final int NOF_FEATURES = 3;
    //NeuralValueFunctionPole valueFunction;
    WeightsDotProductFeatureValueFunction valueFunction;

    @Builder
    public TrainerActorCriticPole(@NonNull EnvironmentPole environment,
                                  @NonNull AgentPole agent,
                                  @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        this.valueFunction = new WeightsDotProductFeatureValueFunction(NOF_FEATURES, parameters.beta());

        //  this.valueFunction=new NeuralValueFunctionPole();
    }

    public void train() {
        Integer n = parameters.stepHorizon();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAllRandom(environment.getParameters()));
            var experienceList = getExperiences();
          //  var experienceListWithReturns =
          //         super.createExperienceListWithReturns(experienceList, parameters.gamma());



            var experienceListInfo = new NStepReturnInfoPole(experienceList, parameters);
            Counter timeCounter = new Counter();
            //for (ExperiencePole experience : experienceListWithReturns) {
            int T= experienceList.size();
            for (int tau = 0; tau < T-n+1; tau++) {
                //Calulate return G
                var resultManySteps = experienceListInfo.getResultManySteps(tau);
                var expAtTau = experienceListInfo.getExperience(tau);

                double G = resultManySteps.sumRewards();

                if (resultManySteps.stateFuture().isPresent()) {
                    G=G+0;      //todo G+γ^n*V(sn')
                } else {
                    log.warning("stateFuture tot present");
                }


                //Update critic
/*
                ArrayRealVector vector = getFeatureVector(experience.state(), environment.getParameters());
                var resultsManySteps = experienceListInfo.getResultManySteps(timeCounter.getCount());
                double valueRef = getValueRef(resultsManySteps);
                valueFunction.update(vector, valueRef);
*/

                //double delta=G-valueFunction.getValue(vector);
                //Update actor
                var gradLogVector = agent.calcGradLogVector(expAtTau.state(),expAtTau.action());

                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * G);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
                timeCounter.increase();
            }
            updateTracker(ei, List.of((double) experienceList.size()));
        }
    }

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
    }

}
