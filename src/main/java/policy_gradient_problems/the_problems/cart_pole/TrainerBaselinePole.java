package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.WeightsDotProductFeatureValueFunction;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;

@Getter
public class TrainerBaselinePole extends TrainerAbstractPole {

    public static final int NOF_FEATURES = 3;
    WeightsDotProductFeatureValueFunction valueFunction;

    @Builder
    public TrainerBaselinePole(@NonNull EnvironmentPole environment,
                              @NonNull AgentParamActorPole agent,
                              @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        this.valueFunction=new WeightsDotProductFeatureValueFunction(NOF_FEATURES, parameters.learningRateCritic());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    super.createExperienceListWithReturns(experienceList,parameters.gamma());
            for (Experience<VariablesPole> experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();

                ArrayRealVector vector= getFeatureVector(experience, environment.getParameters());
                valueFunction.update(vector,vt);

                double delta=vt-valueFunction.getValue(vector);
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * delta);

                agent.changeActor(changeInThetaVector);
                //agent.setActor(agent.getActor().add(changeInThetaVector));
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
