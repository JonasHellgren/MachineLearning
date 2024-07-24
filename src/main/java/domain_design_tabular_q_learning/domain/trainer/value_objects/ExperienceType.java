package domain_design_tabular_q_learning.domain.trainer.value_objects;
import lombok.Builder;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;

@Builder
public record ExperienceType(
        boolean isTerminal,
        boolean isFail
) {

    public static <V> ExperienceType  ofStepReturn(StepReturn<V> sr) {
        return ExperienceType.builder()
                .isTerminal(sr.isTerminal()).isFail(sr.isFail()).build();
    }

    public static ExperienceType nonTerminal() {
        return new ExperienceType(false,false);
    }

}
