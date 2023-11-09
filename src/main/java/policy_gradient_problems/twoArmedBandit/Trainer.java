package policy_gradient_problems.twoArmedBandit;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.helpers.ReturnCalculator;

import java.util.ArrayList;
import java.util.List;


/***
 * agent.theta <- agent.theta+learningRate*gradLog*vt;
 *
 */
@Builder
public class Trainer {

    public static final double DUMMY_VALUE = 0d;
    Environment environment;
    Agent agent;
    @NonNull Integer nofEpisodes;
    @NonNull Integer nofStepsMax;
    @NonNull Double gamma, learningRate;

    public void train() {
        var returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (Experience experience:experienceListWithReturns) {
                var thetaVector = getThetaVector();
                var gradLogVector = getGradLogVector(experience.action());
                double vt = experience.value();
                var thetasNewVector=thetaVector.add(gradLogVector.mapMultiplyToSelf(learningRate*vt));
                agent.setThetas(thetasNewVector.toArray());
            }

        }
    }

    @NotNull
    private ArrayRealVector getThetaVector() {
        return new ArrayRealVector(agent.getThetasAsArray());
    }

    @NotNull
    private ArrayRealVector getGradLogVector(int action) {
        return new ArrayRealVector(agent.getGradLogList(action));
    }

    @NotNull
    private List<Experience> getExperiences() {
        List<Experience> experienceList=new ArrayList<>();
        for (int si = 0; si < nofStepsMax ; si++) {
            int action=agent.chooseAction();
            double reward=environment.step(action);
            experienceList.add(new Experience(action,reward, DUMMY_VALUE));
        }
        return experienceList;
    }

}
