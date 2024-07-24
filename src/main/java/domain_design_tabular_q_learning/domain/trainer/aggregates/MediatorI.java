package domain_design_tabular_q_learning.domain.trainer.aggregates;

import domain_design_tabular_q_learning.domain.trainer.entities.Experience;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerExternal;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public interface MediatorI<V> {
    TrainerExternal<V> getExternal();
    TrainerProperties getProperties();
    Recorder getRecorder();
    void train();
    Episode runEpisode();
    double pRandomAction();
    double fitAgentMemoryFromExperience(Experience<V> experience);
}
