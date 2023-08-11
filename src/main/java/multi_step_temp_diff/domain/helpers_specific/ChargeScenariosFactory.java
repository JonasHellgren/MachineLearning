package multi_step_temp_diff.domain.helpers_specific;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.Scenario;

public class ChargeScenariosFactory {

    static final int TIME = 0;
    public static final double SOL_CRITICAL = 0.55, SOC_MODERATE = 0.9, SOC_MAX = 1.0;
    public static final int POS_SPLIT = 10, POS_CHARGING = 40;;


    public static Scenario<ChargeVariables> BatPos0_At1_BothHighSoC =
            of10Steps("BatPos0_At1_BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(1).socB(SOC_MAX).socA(SOC_MAX).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0_AtPosSplitCriticalSoCA =
            of10Steps("BatPos0_AtPosSplitCriticalSoCA"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(POS_SPLIT).socB(SOC_MAX).socA(SOL_CRITICAL).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0_At20_BothMaxSoC =
            of10Steps("BatPos0_At20_BothHighSoC"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(20).socB(SOC_MAX).socA(SOC_MAX).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPosSplit_AatPos40_BothModerateSoC =
            of10Steps("BatPosSplit_AatPos40_BothModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(POS_SPLIT).posA(POS_CHARGING)
                            .socB(SOC_MODERATE).socA(SOC_MODERATE).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> B3BehindModerateSoC_AatSplitModerateSoC =
            of10Steps("B3BehindModerateSoC_AatSplitModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(7).posA(POS_SPLIT)
                            .socB(SOL_CRITICAL).socA(SOC_MODERATE).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> B1BehindCriticalSoC_AatSplitModerateSoC =
            of10Steps("B1BehindCriticalSoC_AatSplitModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(9).posA(POS_SPLIT)
                            .socB(SOL_CRITICAL).socA(SOC_MODERATE).time(TIME)
                            .build()));


    public static Scenario<ChargeVariables> B3BehindCriticalSoC_AatSplitModerateSoC =
            of10Steps("B3BehindCriticalSoC_AatSplitModerateSoC"
                    , new ChargeState(ChargeVariables.builder().posB(7).posA(POS_SPLIT)
                            .socB(SOL_CRITICAL).socA(SOC_MODERATE).time(TIME)
                            .build()));

    public static Scenario<ChargeVariables> BatPos0_At1_BothHighSoC_1000steps =
            of1000Steps("BatPos0_At1_BothHighSoC_1000steps"
                    , new ChargeState(ChargeVariables.builder().posB(0).posA(1).socB(SOC_MAX).socA(SOC_MAX).time(TIME)
                            .build()));


    public static  Scenario<ChargeVariables> of10Steps(String name, StateInterface<ChargeVariables> state) {
        return new Scenario<>(name, state, 10);
    }

    public static  Scenario<ChargeVariables> of1000Steps(String name, StateInterface<ChargeVariables> state) {
            return new Scenario<>(name, state, 1000);
    }

}
