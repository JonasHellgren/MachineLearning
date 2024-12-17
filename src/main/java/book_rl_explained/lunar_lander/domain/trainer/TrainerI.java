package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.RecorderTrainingProgress;

public interface TrainerI {
    RecorderTrainingProgress getRecorder();
    TrainerDependencies getDependencies();
    void train();
}
