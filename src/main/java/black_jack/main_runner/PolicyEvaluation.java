package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerStateValue;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import black_jack.models_memory.MemoryInterface;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import black_jack.result_drawer.GridPanel;
import lombok.extern.java.Log;


/***
 *     Candidate parameter setting:
 *     private static final double ALPHA = 0.9;  //critical parameter setting
 *     private static final boolean NOF_VISITS_FLAG = true;  //critical parameter setting
 */

@Log
public class PolicyEvaluation {

    public static final int NOF_EPISODES = 500_000;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";



    public static void main(String[] args) {
        MemoryInterface<StateObserved> memory = new StateValueMemory();
        playBlackJackManyTimesAndSetValueMemory(memory);

        AverageValueCalculator<StateObserved> ac=new AverageValueCalculator<>();
        String frameTitleNoUsableAce="No usable ace, average value = "+ac.getAverageValue(memory,false);
        String frameTitleUsableAce= "Usable ace, average value = "+ac.getAverageValue(memory,true);
        GridPanel panelNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(frameTitleNoUsableAce,X_LABEL, Y_LABEL);
        GridPanel panelUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(frameTitleUsableAce,X_LABEL, Y_LABEL);

        MemoryShower<StateObserved> ms=new MemoryShower<>();
        ms.showValueMemory(panelNoUsableAce, panelUsableAce, memory);
    }



    private static void playBlackJackManyTimesAndSetValueMemory(MemoryInterface<StateObserved> stateValueMemory) {
        EnvironmentInterface environment = new BlackJackEnvironment();
        PolicyInterface policy = new PolicyHitBelow20();
        EpisodeRunner episodeRunner = new EpisodeRunner(environment, policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
        NumberOfStateVisitsMemory numberOfStateVisitsMemory = new NumberOfStateVisitsMemory();
        LearnerStateValue learner = new LearnerStateValue((StateValueMemory) stateValueMemory, numberOfStateVisitsMemory, ALPHA, NOF_VISITS_FLAG);
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
        if (episodeNumber % 1_00_000 == 0) {
            log.info("i = " + episodeNumber);
        }
    }







}
