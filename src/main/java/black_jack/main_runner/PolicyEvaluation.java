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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/***
 *     Candidate parameter setting:
 *     private static final double ALPHA = 0.9;  //critical parameter setting
 *     private static final boolean NOF_VISITS_FLAG = true;  //critical parameter setting
 */

@Log
public class PolicyEvaluation {

    public static final int NOF_EPISODES = 200_000;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";
    private static final int NOF_DECIMALS_FRAME_TITLE = 2;


    public static void main(String[] args) {
        MemoryInterface stateValueMemory = new StateValueMemory();
        playBlackJackManyTimesAndSetValueMemory(stateValueMemory);

        String frameTitleNoUsableAce="No usable ace, average value = "+getAverageValue(stateValueMemory,false);
        String frameTitleUsableAce= "Usable ace, average value = "+getAverageValue(stateValueMemory,true);
        GridPanel panelNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(frameTitleNoUsableAce,X_LABEL, Y_LABEL);
        GridPanel panelUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(frameTitleUsableAce,X_LABEL, Y_LABEL);
        showValueMemory(panelNoUsableAce, panelUsableAce, stateValueMemory);
    }



    private static void playBlackJackManyTimesAndSetValueMemory(MemoryInterface stateValueMemory) {
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

    private static void showValueMemory(GridPanel panelNoUsableAce, GridPanel panelUsableAce, MemoryInterface<StateObserved> stateValueMemory) {
        setPanel(panelNoUsableAce, stateValueMemory, false);
        setPanel(panelUsableAce, stateValueMemory, true);
        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

    private static void setPanel(GridPanel panel, MemoryInterface<StateObserved> stateValueMemory, boolean usableAce) {
        List<Integer> xSet = StateObserved.getDealerCardList();
        List<Integer> ySet = StateObserved.getHandsSumList();
        for (int y : ySet) {
            for (int x : xSet) {
                double value = stateValueMemory.read(new StateObserved(y, usableAce, x));
                panel.setNumbersAtCell(x,y, value);
            }
        }
        panel.setColorsAtCells();
        panel.setTextCellValues(true);
    }

    private static String getAverageValue(MemoryInterface<StateObserved> stateValueMemory, boolean usableAce) {
        Predicate<StateObserved> p = (usableAce)
                ?s -> s.playerHasUsableAce
                :s -> !s.playerHasUsableAce;
        Set<Double> valueList= stateValueMemory.valuesOf(p);
        double avg= valueList.stream().filter(Objects::nonNull).mapToDouble(v -> v).average().orElse(Double.NaN);
        BigDecimal bd = BigDecimal.valueOf(avg).setScale(NOF_DECIMALS_FRAME_TITLE, RoundingMode.HALF_DOWN);
        return bd.toString();
    }



}
