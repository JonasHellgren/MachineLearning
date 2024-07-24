package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public interface MediatorI {

    TrainerExternal getExternal();
    TrainerProperties getProperties();
    Recorder getRecorder();
    void train();
    Episode runEpisode();
    StateRoad getStartState();
    double pRandomAction();
   // void fitAgentMemoryFromEpisode(Episode episode);
    double fitAgentMemoryFromExperience(Experience experience);
}
