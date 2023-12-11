package policy_gradient_problems.the_problems.cart_pole;

import common.Counter;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.WeightsDotProductFeatureValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

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
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAllRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    super.createExperienceListWithReturns(experienceList, parameters.gamma());

            var experienceListInfo = new NStepReturnInfoPole(experienceList, parameters);
            Counter timeCounter = new Counter();
            for (ExperiencePole experience : experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(), experience.action());
                double vt = experience.value();

                ArrayRealVector vector = getFeatureVector(experience.state(), environment.getParameters());
                var resultsManySteps = experienceListInfo.getResult(timeCounter.getCount());
                double valueRef = getValueRef(resultsManySteps);
                valueFunction.update(vector, valueRef);

                //double delta=vt-valueFunction.getValue(vector);
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * vt);
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
