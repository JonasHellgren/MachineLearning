package maze_domain_design.domain.trainer.value_objects;
import lombok.Builder;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import org.apache.commons.math3.analysis.function.Exp;

@Builder
public record ExperienceType(
        boolean isTerminal,
        boolean isFail
) {

    public static ExperienceType ofStepReturn(StepReturn sr) {
        return ExperienceType.builder().isTerminal(sr.isTerminal()).isFail(sr.isFail()).build();
    }

    public static ExperienceType nonTerminal() {
        return new ExperienceType(false,false);
    }

}
