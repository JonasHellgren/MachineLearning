package policy_gradient_problems.sink_the_ship;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.common.*;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import java.util.List;

public class TrainerActorCriticShip extends TrainerAbstractShip  {

    public static final double VALUE_TERMINAL_STATE = 0;
    TabularValueFunction valueFunction;

    @Builder
    public TrainerActorCriticShip(@NonNull EnvironmentShip environment,
                                @NonNull AgentShip agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters, new TrainingTracker());
        valueFunction=new TabularValueFunction(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setRandomState();
            trainAgentFromExperiences(getExperiences());
            updateTracker(ei);
        }
    }

    private void trainAgentFromExperiences(List<ExperienceContAction> experienceList) {
        double I=1;
        var returnCalculator=new ReturnCalculator();
        var expListWithReturns=returnCalculator.createExperienceListWithReturnsContActions(experienceList,parameters.gamma());
        for (ExperienceContAction experience: expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            valueFunction.updateFromExperienceCont(experience, I*delta, parameters.beta());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate()* I *delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            I = I *parameters.gamma();
        }
    }

    private double calcDelta(ExperienceContAction experience) {
        //double v=valueFunction.getValue(experience.state());
       // double vNext= environment.isTerminal(experience.stateNext())
       //         ? VALUE_TERMINAL_STATE
        //        : valueFunction.getValue(experience.stateNext());
        return experience.reward(); //+parameters.gamma()*vNext-v;
    }

}
