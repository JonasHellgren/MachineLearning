package book_rl_explained.lunar_lander.domain.trainer;

public interface TrainerI {
    TrainerDependencies getDependencies();
    void train();
}
