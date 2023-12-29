package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common.*;
import policy_gradient_problems.common.ReturnCalculatorOld;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.ExperienceContAction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

@Getter
public class TrainerActorCriticShip extends TrainerAbstractShip  {

    public static final double VALUE_TERMINAL_STATE = 0;
    TabularValueFunction valueFunction;

    @Builder
    public TrainerActorCriticShip(@NonNull EnvironmentShip environment,
                                @NonNull AgentShip agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        valueFunction=new TabularValueFunction(EnvironmentShip.POSITIONS.size());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setRandomState();
            trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei,valueFunction);
        }
    }

    private void trainAgentFromExperiences(List<Experience<VariablesShip>> experienceList) {
        double I=1;
        var returnCalculator=new ReturnCalculator<VariablesShip>();
        var expListWithReturns=returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());

        for (Experience<VariablesShip> experience: expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            int pos = experience.state().getVariables().pos();
            valueFunction.updateFromExperienceCont(pos, I*delta, parameters.learningRateCritic());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor()* I *delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            I = I *parameters.gamma();
        }
    }

    private double calcDelta(Experience<VariablesShip> experience) {
        double v=valueFunction.getValue(experience.state().getVariables().pos());
        return experience.reward()+parameters.gamma()*VALUE_TERMINAL_STATE -v;
    }

}
