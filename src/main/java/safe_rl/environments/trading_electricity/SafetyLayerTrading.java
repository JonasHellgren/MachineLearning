package safe_rl.environments.trading_electricity;

import lombok.SneakyThrows;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.SafeChargeOptModel;
import safe_rl.environments.buying_electricity.StateBuying;

/*public class SafetyLayerTrading<V> implements SafetyLayerI<V> {
    public static final double SOC_DUMMY = 0.5;
    public static final double POWER_PROPOSED_DUMMY = 0d;
    public static final double TOLERANCE_OPTIMIZATION = 1e-4;

    SafeTradeOptModel model;
    SettingsTrading settings;

    public SafetyLayerTrading(SettingsTrading settings) {
        this.settings = settings;
        this.model=createModel(settings);
    }

    @SneakyThrows
    @Override
    public Action correctAction(StateI<V> state, Action action) {
        boolean anyViolation = isAnyViolation(state, action);
        double correctedPower= anyViolation
                ? model.correctedPower()
                : action.asDouble();
        return anyViolation
                ? Action.ofDoubleSafeCorrected(correctedPower)
                : Action.ofDouble(correctedPower);
    }

    @Override
    public boolean isAnyViolation(StateI<V> state, Action action) {
        setModel((StateBuying) state, action);
        return model.isAnyViolation();
    }

    private  void setModel(StateBuying state, Action action) {
        model.setSoCAndPowerProposed(state.soc(), action.asDouble());
    }

    private  SafeTradeOptModel createModel(SettingsTrading settings) {
        return SafeTradeOptModel.builder()
                .powerProposed(POWER_PROPOSED_DUMMY).powerMax(settings.powerBattMax())
                .settings(settings)
                .soc(SOC_DUMMY)
                .toleranceOptimization(TOLERANCE_OPTIMIZATION)
                .build();
    }

}*/
