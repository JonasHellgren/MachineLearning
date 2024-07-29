package safe_rl.environments.buying;

import common.list_arrays.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.EnvironmentBuying;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.ArrayList;
import java.util.List;

class TestEnvironmentBuying5HoursIncreasingPriceManySteps {

    EnvironmentBuying environment;
    StateBuying state;

    @BeforeEach
    void init() {
        environment=new EnvironmentBuying(SettingsBuying.new5HoursIncreasingPrice());
        state =StateBuying.of(VariablesBuying.newSoc(0.5));
    }

    @Test
    void givenZeroState_whenManyStepsPower2_thenCorrect() {
        List<Double> powerList=List.of(3d,2d,0d,0d,1e5d);  //end power does not matter
        var rewardList=applyPowerList( powerList);
        double revenue = ListUtils.sumList(rewardList);

        Assertions.assertEquals(powerList.size(),rewardList.size());
        Assertions.assertTrue(ListUtils.findMax(rewardList).orElseThrow()>0);
        Assertions.assertTrue(ListUtils.findMin(rewardList).orElseThrow()<0);
        Assertions.assertTrue(revenue >0);
    }

    @Test
    void givenZeroState_whenManySteps_thenEqualPowerWorse() {
        double revenueOpt = ListUtils.sumList(applyPowerList(List.of(3d,2d,0d,0d,0d)));
        double revenueNonOpt = ListUtils.sumList(applyPowerList(List.of(1d,1d,1d,0d,0d)));
        Assertions.assertTrue(revenueOpt > revenueNonOpt);
    }

    private List<Double> applyPowerList(List<Double> powerList) {
        List<Double> rewardList=new ArrayList<>();
        var state= this.state.copy();
        for (double power: powerList) {
            var sr=environment.step(state, Action.ofDouble(power));
            rewardList.add(sr.reward());
            System.out.println("sr = " + sr);
            if(sr.isFail()) {
                break;
            }
            state.setVariables(sr.state().getVariables());
        }
        return rewardList;
    }


}
