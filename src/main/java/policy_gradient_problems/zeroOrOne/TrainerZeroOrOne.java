package policy_gradient_problems.zeroOrOne;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common.ReturnCalculator;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
public class TrainerZeroOrOne {

    public static final double DUMMY_VALUE = 0d;
    public static final int STATE = 0;
    Environment environment;
    Agent agent;
    @NonNull Integer nofEpisodes;
    @NonNull Integer nofStepsMax;
    @NonNull Double gamma, learningRate;

    public void train() {
        ReturnCalculator returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            List<ExperienceDiscreteAction> experienceList = getExperiences();
            List<ExperienceDiscreteAction> experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (ExperienceDiscreteAction exp:experienceListWithReturns) {
                double gradLog = agent.gradLogPolicy(exp.action());
                double vt = exp.value();
                agent.setTheta(agent.theta+learningRate*gradLog*vt);
            }
        }
    }

    @NotNull
    private List<ExperienceDiscreteAction> getExperiences() {
        List<ExperienceDiscreteAction> experienceList=new ArrayList<>();
        for (int si = 0; si < nofStepsMax ; si++) {
            int action=agent.chooseAction();
            double reward=environment.step(action);
            experienceList.add(new ExperienceDiscreteAction(STATE,action,reward, STATE, DUMMY_VALUE));
        }
        return experienceList;
    }
}
