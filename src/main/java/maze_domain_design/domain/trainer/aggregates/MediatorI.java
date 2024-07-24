package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public interface MediatorI<V> {

    TrainerExternal<V> getExternal();
    TrainerProperties getProperties();
    Recorder getRecorder();
    void train();
    Episode runEpisode();
    //StateI<V> getStartState();
    double pRandomAction();
   // void fitAgentMemoryFromEpisode(Episode<V> episode);
    double fitAgentMemoryFromExperience(Experience<V> experience);
}
