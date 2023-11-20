package policy_gradient_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.helpers.ReturnCalculator;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerWithBaselineSC  extends TrainerAbstractSC {

    ArrayRealVector wVector;   //value function parameters

    @Builder
    public TrainerWithBaselineSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentSC agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters, new TrainingTracker());
        wVector = getZeroVector();
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
                var phiVector= getZeroVectorWithSingleElementAsOne(experience.state());
                double delta = calcDelta(Gt, phiVector);
                updateWeightVector(experience, delta);
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * delta);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei);
        }
    }

    private double calcDelta(double Gt, ArrayRealVector phiVector) {
        double value= phiVector.dotProduct(wVector);
        return Gt -value;
    }


    private static ArrayRealVector getZeroVector() {
        return new ArrayRealVector(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
    }

    private static ArrayRealVector getZeroVectorWithSingleElementAsOne(int indexOneElement) {
        var vector=new ArrayRealVector(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
        vector.setEntry(indexOneElement,1d);
        return vector;
    }

    private void updateWeightVector(Experience experience, double delta) {
        double oldWi=wVector.getEntry(experience.state());
        wVector.setEntry(experience.state(),oldWi+parameters.beta()* delta);
    }

}
