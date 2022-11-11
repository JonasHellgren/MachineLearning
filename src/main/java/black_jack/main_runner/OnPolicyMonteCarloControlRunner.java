package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerStateActionValue;
import black_jack.helper.LearnerStateValue;
import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateCards;
import black_jack.models_cards.StateObserved;
import black_jack.models_episode.Episode;
import black_jack.models_memory.NumberOfStateActionsVisitsMemory;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.policies.PolicyGreedyOnStateActionMemory;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import black_jack.result_drawer.GridPanel;
import lombok.extern.java.Log;

@Log
public class OnPolicyMonteCarloControlRunner {

    public static final int NOF_EPISODES = 500_000;
    private static final double ALPHA = 0.003;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";
    private static final int NOF_DECIMALS_FRAME_TITLE = 2;
    private static final double PROBABILITY_RANDOM_ACTION = 0.05;


    public static void main(String[] args) {
        StateActionValueMemory memory = new StateActionValueMemory();
        playBlackJackManyTimesAndSetValueMemory(memory);

        String frameTitleNoUsableAce="No usable ace, average value = "; //+getAverageValue(valueMemory,false);
        String frameTitleUsableAce= "Usable ace, average value = "; //+getAverageValue(valueMemory,true);
        GridPanel panelNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(frameTitleNoUsableAce,X_LABEL, Y_LABEL);
        GridPanel panelUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(frameTitleUsableAce,X_LABEL, Y_LABEL);

        MemoryShower<StateActionObserved> ms=new MemoryShower<>();
        ms.showValueMemory(panelNoUsableAce, panelUsableAce, memory);


        System.out.println("memory.getStateActionValueMap().size() = " + memory.getStateActionValueMap().size());

    }

    private static void playBlackJackManyTimesAndSetValueMemory(StateActionValueMemory memory) {
        EnvironmentInterface environment = new BlackJackEnvironment();
        PolicyInterface policy = new PolicyGreedyOnStateActionMemory(memory, PROBABILITY_RANDOM_ACTION);
        EpisodeRunner episodeRunner = new EpisodeRunner(environment, policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
        NumberOfStateActionsVisitsMemory visitsMemory = new NumberOfStateActionsVisitsMemory();
        LearnerStateActionValue learner = new LearnerStateActionValue(memory, visitsMemory, ALPHA, NOF_VISITS_FLAG);
        for (int episodeNumber = 0; episodeNumber < NOF_EPISODES; episodeNumber++) {
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
