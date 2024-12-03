package book_rl_explained.lunar_lander.domain.environment;

public interface EnvironmentI {

    StepReturnLunar step(StateLunar state, double action);

}
