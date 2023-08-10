package multi_step_temp_diff.domain.helpers_specific;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.Scenario;

public class ChargeScenariousContainer {

    static final int TIME = 0;
    public static final double SOC_MODERATE = 0.9;


    public static Scenario<ChargeVariables> BatPos0At1BothHighSoC =
            of5Steps("BatPos0At1BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(1).socB(1).socA(1).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0AtPosSplitLowSoCA =
            of5Steps("BatPos0AtPosSplitLowSoCA"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(10).socB(1).socA(0.4).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0At20BothHighSoC =
            of5Steps("BatPos0At20BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(20).socB(1).socA(1).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPosSplitAtPos40BothModerateSoC =
            of5Steps("BatPosSplitAtPos40BothModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(10).posA(40)
                            .socB(SOC_MODERATE).socA(SOC_MODERATE).time(TIME)
                            .build()));

    public static final double SOL_LOW = 0.55;
    public static Scenario<ChargeVariables> BJustBehindLowSoC_AatSplitModerateSoC =
            of5Steps("BatPosSplitAtPos40BothModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(7).posA(10)
                            .socB(SOL_LOW).socA(SOC_MODERATE).time(TIME)
                            .build()));


    public static  Scenario<ChargeVariables> of5Steps(String name, StateInterface<ChargeVariables> state) {
        return new Scenario<>(name, state, 5);
    }

}
