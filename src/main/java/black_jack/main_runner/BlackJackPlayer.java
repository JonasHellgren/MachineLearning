package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerInterface;
import black_jack.models_cards.StateCards;
import black_jack.models_episode.Episode;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.policies.PolicyInterface;
import lombok.extern.java.Log;

/***
 * A compiling class that gathers policy, learner, environment etc to play and learn
 */

@Log
public class BlackJackPlayer {

    private static final int NOF_ITERATIONS_BETWEEN_LOGS = 100_000;
    PolicyInterface policy;
    LearnerInterface learner;
    int nofEpisodes;

    public BlackJackPlayer(PolicyInterface policy, LearnerInterface learner, int nofEpisodes) {
        this.policy = policy;
        this.learner = learner;
        this.nofEpisodes = nofEpisodes;
    }

    public void playAndSetMemory() {
        EnvironmentInterface environment = BlackJackEnvironment.newSameRewardIfAceAndFaceCard();
        EpisodeRunner episodeRunner = new EpisodeRunner(environment, policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
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
        if (episodeNumber % NOF_ITERATIONS_BETWEEN_LOGS == 0) {
            log.info("i = " + episodeNumber);
        }
    }

}
