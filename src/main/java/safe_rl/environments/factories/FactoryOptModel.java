package safe_rl.environments.factories;

import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.SafeChargeOptModel;

public class FactoryOptModel {

    public static final double SOC_DUMMY = 0.5;
    public static final double POWER_PROPOSED_DUMMY = 0d;
    public static final double TOLERANCE_OPTIMIZATION = 1e-4;

    public static SafeChargeOptModel createChargeModel(BuySettings settings) {
        return SafeChargeOptModel.builder()
                .powerProposed(POWER_PROPOSED_DUMMY).powerMax(settings.powerBattMax())
                .settings(settings)
                .soc(SOC_DUMMY)
                .toleranceOptimization(TOLERANCE_OPTIMIZATION)
                .build();
    }
}
