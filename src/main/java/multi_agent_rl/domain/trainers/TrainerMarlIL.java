package multi_agent_rl.domain.trainers;

import lombok.Builder;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.EnvironmentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.Experience;
import multi_agent_rl.domain.value_classes.TrainerParameters;
import multi_agent_rl.helpers.*;
import java.util.List;
import java.util.function.Supplier;

public class TrainerMarlIL<V,O> {

    EnvironmentI<V,O> environment;
    List<AgentI<O>> agents;
    TrainerParameters trainerParameters;
    Supplier<StateI<V,O>> startStateSupplier;
    ExperienceCreator<V,O> experienceCreator;
/*
    MultiStepEpisodeTrainer<V> episodeTrainer;
    ReplayBufferMultiStepExp<V> buffer;
*/

  //  MultiStepActorUpdater<V> actorUpdater;
    MultiStepCriticUpdater<V> criticUpdater;

    @Builder
    public TrainerMarlIL(EnvironmentI<V,O> environment,
                         List<AgentI<O>> agents,
                         TrainerParameters trainerParameters,
                         Supplier<StateI<V,O>> startStateSupplier) {
        this.environment = environment;
        this.agents = agents;
        this.trainerParameters = trainerParameters;
        this.startStateSupplier = startStateSupplier;
        this.experienceCreator=new ExperienceCreator<>(environment,trainerParameters);
    }

    public void train()  {
        for (int i = 0; i < trainerParameters.nofEpisodes(); i++) {
            var experiences = getExperiences();
            System.out.println("experiences.size() = " + experiences.size());

            trainAgentsFromNewExperiences(experiences);


//            addNewExperienceToBuffer();
        //    trainAgentFromOldExperiences();
       //     updateRecorder(experiences);
        }
    }

    List<Experience<V,O>> getExperiences()  {
        return experienceCreator.getExperiences(agents, startStateSupplier.get());
    }
/*

    void addNewExperienceToBuffer() {
        var msRes = episodeTrainer.getMultiStepResultsFromPrevFit();
        Conditionals.executeIfFalse(msRes.orElseThrow().isEmpty(), () ->
                buffer.addAll(msRes.orElseThrow().experienceList()));
    }
*/

    void trainAgentsFromNewExperiences(List<Experience<V,O>> experiences) {
        //var errorList = recorder.recorderTrainingProgress.criticLossTraj();

        for (AgentI<O> agent:agents) {
            MultiStepResultsGenerator<V, O> generator = new MultiStepResultsGenerator<>(trainerParameters, agent);
            var multiStepResults=generator.generate(experiences);

            System.out.println("agent.getId() = " + agent.getId());
            multiStepResults.experienceList().forEach(System.out::println);

            // criticUpdater.update(multiStepResults);
        }

//        actorUpdater.update(multiStepResults);

        //episodeTrainer.trainAgentFromExperiences(experiences, errorList);
    }

}
