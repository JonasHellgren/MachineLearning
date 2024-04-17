package safe_rl.environments.buying_electricity;

import lombok.SneakyThrows;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.safety_layer.SafetyLayerI;

public class SafetyLayerBuying<V> implements SafetyLayerI<V> {

    public static final double SOC_DUMMY = 0.5;
    public static final double POWER_PROPOSED_DUMMY = 0d;
    public static final double TOLERANCE_OPTIMIZATION = 1e-4;

    SafeChargeOptModel model;
    BuySettings settings;

    public SafetyLayerBuying(BuySettings settings) {
        this.settings = settings;
        this.model=createModel(settings);
    }

    @SneakyThrows
    public Action correctAction(StateI<V> state, Action action) {
        double correctedPower= isAnyViolation(state,action)
                ? model.correctedPower()
                : action.asDouble();
        return Action.ofDouble(correctedPower);
    }

    public boolean isAnyViolation(StateI<V> state, Action action) {
        setModel((StateBuying) state, action);
        return model.isAnyViolation();
    }

    private  void setModel(StateBuying state, Action action) {
        model.setSoCAndPowerProposed(state.soc(), action.asDouble());
    }

    private  SafeChargeOptModel createModel(BuySettings settings) {
        return SafeChargeOptModel.builder()
                .powerProposed(POWER_PROPOSED_DUMMY).powerMax(settings.powerBattMax())
                .settings(settings)
                .soc(SOC_DUMMY)
                .toleranceOptimization(TOLERANCE_OPTIMIZATION)
                .build();
    }

}