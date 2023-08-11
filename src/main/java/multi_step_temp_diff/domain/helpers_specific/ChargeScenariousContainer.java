package multi_step_temp_diff.domain.helpers_specific;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.Scenario;

public class ChargeScenariousContainer {

    static final int TIME = 0;
    public static final double SOC_MODERATE = 0.9;


    public static Scenario<ChargeVariables> BatPos0_At1_BothHighSoC =
            of5Steps("BatPos0_At1_BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(1).socB(1).socA(1.0).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0_AtPosSplitLowSoCA =
            of5Steps("BatPos0_AtPosSplitLowSoCA"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(10).socB(1).socA(0.4).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0_At20_BothHighSoC =
            of5Steps("BatPos0_At20_BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(20).socB(1).socA(1).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPosSplit_AatPos40_BothModerateSoC =
            of5Steps("BatPosSplit_AatPos40_BothModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(10).posA(40)
                            .socB(SOC_MODERATE).socA(SOC_MODERATE).time(TIME)
                            .build()));

    public static final double SOL_LOW = 0.55;
    public static Scenario<ChargeVariables> BJustBehindLowSoC_AatSplitModerateSoC =
            of5Steps("BJustBehindLowSoC_AatSplitModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(7).posA(10)
                            .socB(SOL_LOW).socA(SOC_MODERATE).time(TIME)
                            .build()));


    public static  Scenario<ChargeVariables> of5Steps(String name, StateInterface<ChargeVariables> state) {
        return new Scenario<>(name, state, 5);
    }

}
