package multi_agent_rl.domain.value_classes;

import com.google.common.collect.Lists;
import lombok.Builder;
import multi_agent_rl.domain.abstract_classes.ActionJoint;
import multi_agent_rl.domain.abstract_classes.StateI;

import java.util.List;

/**
 * Data container for episode results
 * Every item in experience list is for specific time step
 */

@Builder
public record MultiStepResults<V,O>(
        int nExperiences,  //equal to length of below lists
        List<MultiStepResultItem<V,O>> experienceList
) {

    public static <V,O> MultiStepResults<V,O> create(int nExp) {
        return MultiStepResults.<V,O>builder()
                .nExperiences(nExp)
                .experienceList(Lists.newArrayListWithCapacity(nExp))
                .build();
    }


    public boolean isEmpty() {
        return nExperiences==0;
    }

    public MultiStepResultItem<V,O> experienceAtStep(int step) {
        return experienceList.get(step);
    }

    public StateI<V,O> stateAtStep(int step) {return experienceAtStep(step).state(); }

    public boolean isFutureOutsideOrTerminalAtStep(int step) {
        return experienceAtStep(step).isStateFutureTerminalOrNotPresent();
    }

    public double valueTarAtStep(int step) {return experienceAtStep(step).valueTarget(); }

    public double advantageAtStep(int step) {return experienceAtStep(step).advantage(); }

    public ActionJoint actionAtStep(int step) {return experienceAtStep(step).action(); }


    public void add(MultiStepResultItem<V,O> experience) {
        experienceList.add(experience);
    }


}
