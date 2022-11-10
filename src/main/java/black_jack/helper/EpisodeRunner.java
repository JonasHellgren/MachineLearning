package black_jack.helper;

import black_jack.environment.EnvironmentInterface;
import black_jack.enums.CardAction;
import black_jack.models_episode.Episode;
import black_jack.models_cards.StateCards;
import black_jack.environment.StepReturnBJ;
import black_jack.policies.PolicyInterface;

public class EpisodeRunner {

    EnvironmentInterface environment;
    PolicyInterface policy;

    Episode episode;

    public EpisodeRunner(EnvironmentInterface environment, PolicyInterface policy) {
        this.environment = environment;
        this.policy = policy;
        this.episode= new Episode();
    }

    public Episode play(StateCards cards) {

        episode.clear();
        boolean stopPlaying;
        do {
            CardAction action = policy.hitOrStick(cards.observeState());
            StepReturnBJ returnBJ = environment.step(action, cards);
            stopPlaying= returnBJ.stopPlaying;
            episode.add(cards.observeState(), action, returnBJ.reward);
            cards.copy(returnBJ);
        } while (!stopPlaying);
        return episode;
    }

}
