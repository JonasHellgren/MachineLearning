package policygradient.common;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import policy_gradient_problems.the_problems.short_corridor.VariablesSC;

import java.util.ArrayList;
import java.util.List;

public class TestReturnCalculator {

    ReturnCalculator<VariablesSC> returnCalculator;

    @BeforeEach
    public void init() {
        returnCalculator=new ReturnCalculator<>();
    }

    //rewards=[0,1,1], gamma=1 => returns=[2,2,1]
    @Test
    public void givenReturns0d1d1dGamma1_thenCorrect() {
        var rewards= List.of(0d,1d,1d);
        double gamma = 1d;
        var returns = getReturns(rewards, gamma);
        Assertions.assertEquals(List.of(2d,2d,1d),returns);
    }

    //rewards=[0,1,1], gamma=0d5 => returns=[2,1,0.25]
    @Test
    public void givenReturns0d1d1dGamma0d5_thenCorrect() {
        var rewards= List.of(0d,1d,1d);
        double gamma = 0.5d;
        var returns = getReturns(rewards, gamma);
        Assertions.assertEquals(List.of(2d,1d,0.25d),returns);
    }

    //list = [10 10 10], df=0.5 => listDf=[1*df^0 1*df^1 1*df^2]
    // => product(returns(list),listDf)=[30,10,2.5]
    @Test
    public void givenReturns00d10d10dGamma0d5_thenCorrect() {
        var rewards= List.of(10d,10d,10d);
        double gamma = 0.5d;
        var returns = getReturns(rewards, gamma);
        Assertions.assertEquals(List.of(30d,10d,2.5d),returns);
    }

    @Test
    public void givenEmpty_thenCorrect() {
        List<Double> rewards= new ArrayList<>();
        double gamma = 0.5d;
        var returns = getReturns(rewards, gamma);
        Assertions.assertTrue(returns.isEmpty());
    }


    @NotNull
    private List<Double> getReturns(List<Double> rewards, double gamma) {
        List<Experience<VariablesSC>> experienceList= rewards.stream()
                .map(r -> {
                    StateSC dummyState = StateSC.newDefault();
                    Action dummyAction = Action.ofInteger(0);
                    return Experience.of(dummyState, dummyAction,r, dummyState);
                })
                .toList();
        var expListWithReturns=returnCalculator.createExperienceListWithReturns(experienceList, gamma);
        return expListWithReturns.stream().map(e -> e.value()).toList();
    }

}
