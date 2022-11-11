package black_jack.main_runner;

import black_jack.models_memory.ValueMemory;
import black_jack.result_drawer.GridPanel;

public class OnPolicyMonteCarloControlRunner {

    public static final int NOF_EPISODES = 200_000;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";
    private static final int NOF_DECIMALS_FRAME_TITLE = 2;


    public static void main(String[] args) {
        ValueMemory valueMemory = new ValueMemory();
     /*   playBlackJackManyTimesAndSetValueMemory(valueMemory);

        String frameTitleNoUsableAce="No usable ace, average value = "+getAverageValue(valueMemory,false);
        String frameTitleUsableAce= "Usable ace, average value = "+getAverageValue(valueMemory,true);
        GridPanel panelNoUsableAce = createNoUsableAceFrameAndPanel(frameTitleNoUsableAce);
        GridPanel panelUsableAce = createUsableAceFrameAndPanel(frameTitleUsableAce);
        showValueMemory(panelNoUsableAce, panelUsableAce, valueMemory);  */
    }

}
