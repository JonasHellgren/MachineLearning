package safe_rl.environments.factories;

import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.SafeChargeOptModel;
import safe_rl.environments.buying_electricity.VariablesBuying;
import safe_rl.environments.trading_electricity.SafeTradeOptModel;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

public class FactoryOptModel {

    private FactoryOptModel() {
    }

    public static final double SOC_DUMMY = 0.5;
    public static final double POWER_PROPOSED_DUMMY = 0d;
    public static final double TOLERANCE_OPTIMIZATION = 1e-4;
    public static final double TIME_NEW_DUMMY = 0d;

    public static SafeChargeOptModel<VariablesBuying> createChargeModel(SettingsBuying settings) {
        return SafeChargeOptModel.<VariablesBuying>builder()
                .powerProposed(POWER_PROPOSED_DUMMY).powerMax(settings.powerBattMax())
                .settings(settings)
                .soc(SOC_DUMMY)
                .toleranceOptimization(TOLERANCE_OPTIMIZATION)
                .build();
    }

    public static SafeTradeOptModel<VariablesTrading> createTradeModel(SettingsTrading settings) {
        return SafeTradeOptModel.<VariablesTrading>builder()
                .powerMin(-settings.powerBattMax()).powerMax(settings.powerBattMax())
                .settings(settings)
                .socTerminalMin(0d)
                .timeNew(TIME_NEW_DUMMY)
                .soc(SOC_DUMMY)
                .toleranceOptimization(TOLERANCE_OPTIMIZATION)
                .build();

    }
}
