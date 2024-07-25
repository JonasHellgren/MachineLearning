package domain_design_tabular_q_learning.domain.trainer.aggregates;

import domain_design_tabular_q_learning.domain.trainer.entities.Experience;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerExternal;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public interface MediatorI<V,A> {
    TrainerExternal<V,A> getExternal();
    TrainerProperties getProperties();
    Recorder getRecorder();
    void train();
    Episode<V,A> runEpisode();
    double pRandomAction();
    double fitAgentMemoryFromExperience(Experience<V,A> experience);
}
