package policygradient.cart_pole;

import org.junit.jupiter.api.*;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;
import java.util.ArrayList;
import java.util.List;
import static common.ListUtils.*;

public class TestNStepReturnInfoPole {

    public static final double REWARD = 1d;
    NStepReturnInfo<VariablesPole> listInfoPole;
    TrainerParameters trainerParameters;

    @BeforeEach
    public void init() {
        trainerParameters=TrainerParameters.builder().gamma(0.5).stepHorizon(5).build();
        List<Experience<VariablesPole>> experiencePoleList=new ArrayList<>();
        for (int x = 0; x < 10 ; x++) {
            experiencePoleList.add(expOf(x));
        }
        listInfoPole=new NStepReturnInfo<>(experiencePoleList,trainerParameters);
    }


    @Test
    public void whenTStartIsZero_thenCorrect() {
        var result=listInfoPole.getResultManySteps(0);
        System.out.println("result = " + result);

        double expectedDiscSum = getExpectedDiscSum(5);
        Assertions.assertEquals(expectedDiscSum,result.sumRewardsNSteps);
        Assertions.assertFalse(result.isEndOutside);
        Assertions.assertEquals(4+1,result.stateFuture.getVariables().x());
    }

    @Test
    public void whenTStartIsFive_thenCorrect() {
        var result=listInfoPole.getResultManySteps(5);
        System.out.println("result = " + result);

        double expectedDiscSum = getExpectedDiscSum(5);
        Assertions.assertEquals(expectedDiscSum,result.sumRewardsNSteps);
        Assertions.assertFalse(result.isEndOutside);
        Assertions.assertEquals(9+1,result.stateFuture.getVariables().x());
    }

    @Test
    public void whenTStartIsSix_thenCorrect() {
        var result=listInfoPole.getResultManySteps(6);
        System.out.println("result = " + result);

        double expectedDiscSum = getExpectedDiscSum(4);
        Assertions.assertEquals(expectedDiscSum,result.sumRewardsNSteps);
        Assertions.assertTrue(result.isEndOutside);
    }

    @Test
    public void whenTStartIs15_thenCorrect() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> listInfoPole.getResultManySteps(15));
    }


    private static Experience<VariablesPole> expOf(double x) {
        var state=StatePole.builder().x(x).build();
        var stateNext= StatePole.builder().x(x+1).build();
        return new Experience<>(state, Action.ofInteger(0), REWARD, stateNext, false, 0);
    }

    private double getExpectedDiscSum(int listLength) {
        return discountedSum(createListWithEqualElementValues(listLength, 1), trainerParameters.gamma());
    }


}
