package policy_gradient_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.helpers.ReturnCalculator;

@Getter
public class TrainerWithBaselineSC  extends TrainerAbstractSC {

    ArrayRealVector wVector;   //for value function

    @Builder
    public TrainerWithBaselineSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentSC agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters, new TrainingTracker());
        wVector = getZeroVector();
    }

    @NotNull
    private static ArrayRealVector getZeroVector() {
        return new ArrayRealVector(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
    }

    public void train() {
        var returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
            for (Experience experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double Gt = experience.value();
                ArrayRealVector phiVector=getZeroVector();
                phiVector.setEntry(experience.state(),1d);
                double value=phiVector.dotProduct(wVector);
                double oldWi=wVector.getEntry(experience.state());
                double delta=Gt-value;
                wVector.setEntry(experience.state(),oldWi+parameters.beta()*delta);

                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * delta);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei);
        }
    }

}
