package black_jack.main_runner;

import black_jack.helper.LearnerStateValue;
import black_jack.models_cards.*;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.policies.PolicyHitBelow20;
import black_jack.result_drawer.GridPanel;
import lombok.extern.java.Log;

/***
 *     Candidate parameter setting:
 *     private static final double ALPHA = 0.9;  //critical parameter setting
 *     private static final boolean NOF_VISITS_FLAG = true;  //critical parameter setting
 */

@Log
public class PolicyEvaluationRunner {

    public static final int NOF_EPISODES = 500_000;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";

    public static void main(String[] args) {
        StateValueMemory memory = new StateValueMemory();
        NumberOfStateVisitsMemory numberOfStateVisitsMemory = new NumberOfStateVisitsMemory();

        BlackJackPlayer player=new BlackJackPlayer(
                new PolicyHitBelow20(),
                new LearnerStateValue(memory, numberOfStateVisitsMemory, ALPHA, NOF_VISITS_FLAG),
                NOF_EPISODES);
        player.playAndSetMemory();

        AverageValueCalculator<StateObserved> ac=new AverageValueCalculator<>();
        String frameTitleNoUsableAce="No usable ace, average value = "+ac.getAverageValue(memory,false);
        String frameTitleUsableAce= "Usable ace, average value = "+ac.getAverageValue(memory,true);
        GridPanel panelNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(frameTitleNoUsableAce,X_LABEL, Y_LABEL);
        GridPanel panelUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(frameTitleUsableAce,X_LABEL, Y_LABEL);

        MemoryShower<StateObserved> ms=new MemoryShower<>();
        ms.showValueMemory(panelNoUsableAce, panelUsableAce, memory);
    }


}
