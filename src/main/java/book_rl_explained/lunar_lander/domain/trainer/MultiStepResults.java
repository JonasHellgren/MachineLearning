package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import com.google.common.collect.Lists;
import lombok.Builder;

import java.util.List;

@Builder
public record MultiStepResults(
        int nExperiences,  //equal to length of below lists
        List<MultiStepResultItem> experienceList
) {

    public static MultiStepResults create(int nExp) {
        return MultiStepResults.builder()
                .nExperiences(nExp)
                .experienceList(Lists.newArrayListWithCapacity(nExp))
                .build();
    }


    public boolean isEmpty() {
        return nExperiences == 0;
    }

    public MultiStepResultItem experienceAtStep(int step) {
        return experienceList.get(step);
    }

    public StateLunar stateAtStep(int step) {
        return experienceAtStep(step).state();
    }

    public boolean isFutureOutsideOrTerminalAtStep(int step) {
        return experienceAtStep(step).isStateFutureTerminalOrNotPresent();
    }

    public double valueTarAtStep(int step) {
        return experienceAtStep(step).valueTarget();
    }

    public double advantageAtStep(int step) {
        return experienceAtStep(step).advantage();
    }

    public double actionAppliedAtStep(int step) {
        return experienceAtStep(step).action();
    }

    public void add(MultiStepResultItem experience) {
        experienceList.add(experience);

    }
}
