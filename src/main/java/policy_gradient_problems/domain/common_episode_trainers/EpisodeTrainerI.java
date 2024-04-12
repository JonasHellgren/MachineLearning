package policy_gradient_problems.domain.common_episode_trainers;

import policy_gradient_problems.domain.value_classes.Experience;

import java.util.List;

public interface EpisodeTrainerI<V> {
    void trainAgentFromExperiences(List<Experience<V>> experienceList);
}
