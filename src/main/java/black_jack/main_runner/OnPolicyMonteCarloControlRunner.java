package black_jack.main_runner;

import black_jack.helper.LearnerStateActionValue;
import black_jack.models_cards.StateObservedAction;
import black_jack.models_memory.*;
import black_jack.policies.PolicyGreedyOnStateActionMemory;
import black_jack.result_drawer.GridPanel;
import lombok.extern.java.Log;

@Log
public class OnPolicyMonteCarloControlRunner {

    public static final int NOF_EPISODES = 2_000_000;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";
    private static final double PROBABILITY_RANDOM_ACTION = 0.02;


    public static void main(String[] args) {
        StateActionValueMemory memory = new StateActionValueMemory();
        NumberOfStateActionsVisitsMemory visitsMemory = new NumberOfStateActionsVisitsMemory();

        BlackJackPlayer player=new BlackJackPlayer(
                new PolicyGreedyOnStateActionMemory(memory, PROBABILITY_RANDOM_ACTION),
                new LearnerStateActionValue(memory, visitsMemory, ALPHA, NOF_VISITS_FLAG),
                NOF_EPISODES);
        player.playAndSetMemory();

        AverageValueCalculator<StateObservedAction> ac=new AverageValueCalculator<>();
        GridPanel panelNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(
                "No usable ace, average value = "+ac.getAverageValue(memory,false),  //frame title
                X_LABEL,Y_LABEL);
        GridPanel panelUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(
                "Usable ace, average value = "+ac.getAverageValue(memory,true),
                X_LABEL,Y_LABEL);

        MemoryShower<StateObservedAction> ms=new MemoryShower<>();
        ms.showValueMemory(panelNoUsableAce, panelUsableAce, memory);

        GridPanel panelPolicyNoUsableAce = FrameAndPanelCreater.createNoUsableAceFrameAndPanel(
                "No usable ace policy (hit=1)",
                X_LABEL,Y_LABEL);
        GridPanel panelPolicyUsableAce = FrameAndPanelCreater.createUsableAceFrameAndPanel(
                "Usable ace policy",
                X_LABEL,Y_LABEL);

        MemoryShower<StateObservedAction> ps=new MemoryShower<>();
        ps.showPolicy(panelPolicyNoUsableAce, panelPolicyUsableAce, memory);






    }




}
