package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerInterface;
import black_jack.helper.LearnerStateActionValue;
import black_jack.models_cards.StateCards;
import black_jack.models_cards.StateObservedAction;
import black_jack.models_episode.Episode;
import black_jack.models_memory.MemoryInterface;
import black_jack.models_memory.NumberOfStateActionsVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.policies.PolicyGreedyOnStateActionMemory;
import black_jack.policies.PolicyInterface;
import lombok.extern.java.Log;

@Log
public class BlackJackPlayer {

    PolicyInterface policy;
    LearnerInterface learner;
    int nofEpisodes;

    public BlackJackPlayer(PolicyInterface policy, LearnerInterface learner, int nofEpisodes) {
        this.policy = policy;
        this.learner = learner;
        this.nofEpisodes = nofEpisodes;
    }

    public   void playAndSetValueMemory(MemoryInterface memory) {
        EnvironmentInterface environment = new BlackJackEnvironment();
       // PolicyInterface policy = new PolicyGreedyOnStateActionMemory((StateActionValueMemory) memory, PROBABILITY_RANDOM_ACTION);
        EpisodeRunner episodeRunner = new EpisodeRunner(environment, policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
        NumberOfStateActionsVisitsMemory visitsMemory = new NumberOfStateActionsVisitsMemory();
        //LearnerStateActionValue learner = new LearnerStateActionValue((StateActionValueMemory) memory, visitsMemory, ALPHA, NOF_VISITS_FLAG);
        for (int episodeNumber = 0; episodeNumber < nofEpisodes; episodeNumber++) {
            sometimeLogEpisodeNumber(episodeNumber);
            StateCards cards = StateCards.newRandomPairs();
            Episode episode = episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemoryFromEpisodeReturns(returnsForEpisode);
        }
    }


    private static void sometimeLogEpisodeNumber(int episodeNumber) {
        if (episodeNumber % 100_000 == 0) {
            log.info("i = " + episodeNumber);
        }
    }

}
