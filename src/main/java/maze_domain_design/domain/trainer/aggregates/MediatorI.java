package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public interface MediatorI {

    TrainerExternal getExternal();
    TrainerProperties getProperties();
    Recorder getRecorder();
    void train();
    Episode createEpisode();
    State getStartState();
    void fitAgentMemoryFromEpisode(Episode episode);
    void updateRecorder();
}
