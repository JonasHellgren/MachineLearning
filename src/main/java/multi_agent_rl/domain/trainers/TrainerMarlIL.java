package multi_agent_rl.domain.trainers;

import com.joptimizer.exception.JOptimizerException;
import common.other.Conditionals;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.EnvironmentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.Experience;
import multi_agent_rl.domain.value_classes.TrainerParameters;
import multi_agent_rl.helpers.*;

import java.util.List;
import java.util.function.Supplier;

public class TrainerMarlIL<V> {

    EnvironmentI<V> environment;
    List<AgentI<V>> agents;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    ExperienceCreator<V> experienceCreator;
/*
    MultiStepEpisodeTrainer<V> episodeTrainer;
    ReplayBufferMultiStepExp<V> buffer;
*/

    MultiStepResultsGenerator<V> generator;
    MultiStepActorUpdater<V> actorUpdater;
    MultiStepCriticUpdater<V> criticUpdater;

    public void train() throws JOptimizerException {
        for (int i = 0; i < trainerParameters.nofEpisodes(); i++) {
            var experiences = getExperiences();
            trainAgentFromNewExperiences(experiences);
//            addNewExperienceToBuffer();
        //    trainAgentFromOldExperiences();
       //     updateRecorder(experiences);
        }
    }

    List<Experience<V>> getExperiences()  {
        return experienceCreator.getExperiences(agents, startStateSupplier.get());
    }
/*

    void addNewExperienceToBuffer() {
        var msRes = episodeTrainer.getMultiStepResultsFromPrevFit();
        Conditionals.executeIfFalse(msRes.orElseThrow().isEmpty(), () ->
                buffer.addAll(msRes.orElseThrow().experienceList()));
    }
*/

    void trainAgentFromNewExperiences(List<Experience<V>> experiences) {
        //var errorList = recorder.recorderTrainingProgress.criticLossTraj();

      /*  var multiStepResults=generator.generate(experiences);
        actorUpdater.update(multiStepResults);
        criticUpdater.update(multiStepResults);
*/
        //episodeTrainer.trainAgentFromExperiences(experiences, errorList);
    }

}
