package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import com.google.common.collect.Lists;
import lombok.Builder;

import java.util.List;

@Builder
public record MultiStepResults(
        int nExperiences,  //equal to length of below lists
        List<MultiStepResult> results
) {

    public static MultiStepResults create(int nExp) {
        return MultiStepResults.builder()
                .nExperiences(nExp)
                .results(Lists.newArrayListWithCapacity(nExp))
                .build();
    }

    public void add(MultiStepResult experience) {
        results.add(experience);
    }

    public boolean isEmpty() {
        return nExperiences == 0;
    }

    public MultiStepResult experienceAtStep(int step) {
        return results.get(step);
    }

    public boolean isFutureOutsideOrTerminalAtStep(int step) {
        return experienceAtStep(step).isStateFutureTerminalOrNotPresent();
    }

    public StateLunar stateAtStep(int step) {
        return experienceAtStep(step).state();
    }

    public double actionAtStep(int step) {
        return experienceAtStep(step).action();
    }


    public double valueTarAtStep(int step) {
        return experienceAtStep(step).valueTarget();
    }


    public double advantageAtStep(int step) {
        return experienceAtStep(step).advantage();
    }


    public double tdErrorAtStep(int step) {
        return experienceAtStep(step).tdError();
    }

    @Override
    public String toString() {
        var sb=new StringBuilder();
        String lineSep = System.lineSeparator();
        sb.append("nExperiences=").append(nExperiences).append(lineSep);
        for (MultiStepResult resultItem:results) {
            sb.append(resultItem).append(lineSep);
        }
        return sb.toString();
    }


}
