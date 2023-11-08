package policy_gradient_problems.twoArmedBandit;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
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
        ReturnCalculator returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            List<Experience> experienceList = getExperiences();
            List<Experience> experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (Experience exp:experienceListWithReturns) {
                RealVector thetas = new ArrayRealVector(agent.getThetasAsArray());
                ArrayRealVector gradLog = new ArrayRealVector(agent.getGradLogList(exp.action()));
                double vt = exp.value();
                RealVector thetasNew=thetas.add(gradLog.mapMultiplyToSelf(learningRate*vt));
                agent.setThetaList(ListUtils.arrayPrimitiveDoublesToList(thetasNew.toArray()));
            }

        }
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
