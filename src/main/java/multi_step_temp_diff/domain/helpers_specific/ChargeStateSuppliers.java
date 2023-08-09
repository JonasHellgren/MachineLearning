package multi_step_temp_diff.domain.helpers_specific;

import common.RandUtils;
import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ChargeStateSuppliers {

    public static final int TIME = 0;
    public static final int TRAP_POS = 29;
    public static final double HIGH_SOC = 0.9;
    ChargeEnvironmentSettings settings;


    public  ChargeState bTrappedAHasRandomSitePosAndRandomSoC() {
        return new ChargeState(ChargeVariables.builder()
                .posB(TRAP_POS).posA(randomSitePos())
                .socA(randomSoC()).socB(HIGH_SOC)
                .time(TIME)
                .build());
    }

    public double randomSoC() {
        return RandUtils.getRandomDouble(settings.socBad(), settings.socMax());
    }

    public int randomSitePos() {
        RandUtils<Integer> randUtils=new RandUtils<>();
        List<Integer> es = new ArrayList<>(settings.siteNodes());
        return randUtils.getRandomItemFromList(es);
    }


    public ChargeState randomDifferentSitePositionsAndRandomSoCs() {
        ChargeStateSuppliers stateSuppliers=new ChargeStateSuppliers(settings);
        int posA = stateSuppliers.randomSitePos() ,posB = stateSuppliers.randomSitePos();
        while (posB==posA) {
            posB = stateSuppliers.randomSitePos();
        }
        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(stateSuppliers.randomSoC()).socB(stateSuppliers.randomSoC())
                .build());
    }


}
