package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.helpers.WeightsDotProductFeatureValueFunction;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

/**
 * Not clean having valueFunction in trainer, but too much hassle creating specific agent
 * Baseline is not worth it, mostly similar to Vanilla version in structure and performance
 */

@Getter
public final class TrainerParamActorParamCriticPole extends TrainerAbstractPole {
    public static final int NOF_FEATURES = 3;
    static final double LEARNING_RATE = 0.01;


    AgentParamActorI<VariablesPole> agent;
    WeightsDotProductFeatureValueFunction valueFunction;

    @Builder
    public TrainerParamActorParamCriticPole(@NonNull EnvironmentPole environment,
                                            @NonNull AgentParamActorI<VariablesPole> agent,
                                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
        this.valueFunction=new WeightsDotProductFeatureValueFunction(NOF_FEATURES, LEARNING_RATE);
    }

    @Override
    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences(agent);
            var rc=new ReturnCalculator<VariablesPole>();
            var experienceListWithReturns =rc.createExperienceListWithReturns(experienceList, parameters.gamma());
            for (Experience<VariablesPole> experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                ArrayRealVector vector= getFeatureVector(experience, environment.getParameters());
                valueFunction.update(vector,vt);
                double delta=vt-valueFunction.getValue(vector);
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateNonNeuralActor() * delta);
                agent.changeActor(changeInThetaVector);
            }
            updateTracker(ei, experienceList);
        }
    }

    @NotNull
    public static ArrayRealVector getFeatureVector(Experience<VariablesPole> experience, ParametersPole p) {
        StatePole stateCasted=(StatePole) experience.state();
        return new ArrayRealVector(new double[]{
                1d,
                Math.abs(stateCasted.angle())/p.angleMax(),
                Math.abs(stateCasted.x())/p.xMax()
        });
    }


}
