package multi_step_temp_diff.domain.helpers_specific;

import common.other.RandUtilsML;
import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
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
        return RandUtilsML.getRandomDouble(settings.socBad(), settings.socMax());
    }

    public double randomSoCAbove(double socMin) {
        return RandUtilsML.getRandomDouble(socMin, settings.socMax());
    }


    public int randomSitePos() {
        RandUtilsML<Integer> randUtils=new RandUtilsML<>();
        List<Integer> es = new ArrayList<>(settings.siteNodes());
        return randUtils.getRandomItemFromList(es);
    }


    public ChargeState randomDifferentSitePositionsAndRandomSoCs() {
        ChargeStateSuppliers stateSuppliers=new ChargeStateSuppliers(settings);
        int posA = stateSuppliers.randomSitePos();
        int posB = posBDifferingFromPosA(posA);
        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(stateSuppliers.randomSoC()).socB(stateSuppliers.randomSoC())
                .build());
    }

    private int posBDifferingFromPosA(int posA) {
        int posB = randomSitePos();
        while (posB== posA) {
            posB = randomSitePos();
        }
        return posB;
    }

    public ChargeState randomDifferentSitePositionsAndMaxSoC() {
        int posA = randomSitePos();
        int posB = posBDifferingFromPosA(posA);
        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(settings.socMax()).socB(settings.socMax())
                .build());
    }

    public ChargeState randomDifferentSitePositionsAndHighSoC() {
        int posA = randomSitePos();
        int posB = posBDifferingFromPosA(posA);
        double socLowest=(settings.socMax()-settings.socBad())/2d;
        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(randomSoCAbove(socLowest)).socB(randomSoCAbove(socLowest))
                .build());
    }


}
