package multi_step_temp_diff.domain.test_helpers;

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
        return RandUtils.getRandomDouble(0, 1);
    }

    public int randomSitePos() {
        RandUtils<Integer> randUtils=new RandUtils<>();
        List<Integer> es = new ArrayList<>(settings.siteNodes());
        return randUtils.getRandomItemFromList(es);
    }


}
