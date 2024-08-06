package safe_rl.environments.trading_electricity;

import lombok.AllArgsConstructor;
import lombok.Builder;

import static java.lang.Math.abs;

@AllArgsConstructor
public class TradeStateUpdater {

    @Builder
    public record UpdaterRes(
            StateTrading stateNew, double dSoh
    ) {}

    SettingsTrading settings;

    public UpdaterRes update(StateTrading s0, double aFcrLumped, double power) {
        var s=settings;
        double powerFcrAvg= s.powerFcrAvg(aFcrLumped, s0.soc());
        double dSoc=(power+powerFcrAvg)*s.dt()/s.energyBatt();
        double dSoh=-abs(power*s.dt())/(s.energyBatt()*s.nCyclesLifetime());
        var stateNew=(StateTrading)  s0.copyWithDtimeDsocDsoh(s.dt(),dSoc,dSoh);
        return UpdaterRes.builder()
                .stateNew(stateNew).dSoh(dSoh)
                .build();
    }

    public boolean isTerminalOld(StateTrading s0) {
        return s0.time()+ settings.dt() > settings.timeEnd();
    }

    public boolean isTerminal(StateTrading s) {
        return s.time() > settings.timeEnd();
    }


}
