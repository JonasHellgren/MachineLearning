package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.environment.StepReturnLunar;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import book_rl_explained.lunar_lander.domain.trainer.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

/****
 *  rewards of experiences: 1, 0, 1, 1, 1  (exp 4 goes to 5, exp 5 is terminal)
 */

class TestMultiStepResultsGenerator {
    public static final int N_FITS = 100;
    public static final int N_EPOCHS = 20;
    public static final double TOL_CRITIC = 0.01;

    MultiStepResultsGenerator generator;
    TrainerDependencies dependencies;
    List<ExperienceLunar> experiences;

    @BeforeEach
    void init() {
        generator = createGenerator(4, 1.0);
        var exp0 = expNotFail(0, false);
        var exp1 = expNotFail(1, false);
        var exp1tTerm = expNotFail(1, true);
        experiences = List.of(exp1, exp0, exp1, exp1, exp1tTerm);
    }

    private MultiStepResultsGenerator createGenerator(int stepHorizon, double gamma) {
        var ep = LunarProperties.defaultProps();
        var p = AgentParameters.defaultParams(ep).withLearningRateCritic(0.5);
        var trainerParameters = TrainerParameters.newDefault().withStepHorizon(stepHorizon).withGamma(gamma);
        dependencies = TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.createDefault())
                .trainerParameters(trainerParameters)
                .startStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep))
                .build();

        return MultiStepResultsGenerator.of(dependencies);
    }

    @Test
    void whenReadingCritic_thenZeroValue() {
        var agent = dependencies.agent();
        double val = agent.readCritic(StateLunar.zeroPosAndSpeed());
        Assertions.assertEquals(0, val);
    }

    @Test
    void whenGenerating_thenCorrectNofExp() {
        var multiStepResults = generator.generate(experiences);
        Assertions.assertEquals(5, multiStepResults.nExperiences());
    }

    @ParameterizedTest   //step, value, advantage, isTerminalOrOutSide
    @CsvSource({
            "0, 1,1,false",
            "1, 0,0,false",
            "2, 1,1,false"
    })
    void givenStepHorizon1_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(1, 1.0);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2,2,false",
            "1, 2,2,false",  //exp 1+3=4 is not terminalOrOutside
            "2, 3,3,true"    //exp 2+3=5 is terminalOrOutside
    })
    void givenStepHorizon3_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(3, 1.0);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 4,4,true",  //exp 0+5=5 is terminalOrOutside
            "1, 3,3,true",  //exp 1+5=6 is terminalOrOutside
            "2, 3,3,true"   //exp 2+5=7 is terminalOrOutside
    })
    void givenStepHorizon5_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(5, 1.0);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
   }

    @Test
    void whenReadingFittedCritic_thenOneValue() {
        var agent = dependencies.agent();
        fitMemory(agent, 1.0);
        double val = agent.readCritic(StateLunar.zeroPosAndSpeed());
        Assertions.assertEquals(1.0, val, TOL_CRITIC);
    }

  //rewards of experiences: 1, 0, 1, 1, 1  (exp 4 goes to 5, exp 5 is terminal)

    @ParameterizedTest   //step, value, isTerminalOrOutside advantage=sumRewards+value(sFut)-value(s)=value-value(s)
    @CsvSource({
            "0, 3,2,false",  //sumRewards=2, value(sFut) = 1 => value=3,  adv=3-1=2
            "1, 3,2,false",  //sumRewards=2, value(sFut) = 1 => value=3,  adv=3-1=2
            "2, 3,2,true",   //sumRewards=3, value(sFut) = 0 => value=3,  adv=3-1=2
            "4, 1,0,true"   //sumRewards=1, value(sFut) = 0 => value=1,  adv=1-1=0
    })
    void givenStepHorizon3AndCriticValue1_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(3, 1.0);
        fitMemory(dependencies.agent(), 1.0);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1.25,1.25,false",  //sumRewards=1+0+.25, value(sFut) = 0 => value=1.25,  adv=1.5-0
            "1, 0.75,0.75,false",   //sumRewards=0+0.5+0.25, value(sFut) = 0 => value=0.75,  adv=0.75-1
    })
    void givenStepHorizon1AndCriticValue0AndGamma0dot5_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(3, 0.5);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1.375,0.375,false",  //sumRewards=1+0+.25, value(sFut) = 0.5^3*1=1/8 => value=1.25+1/8=1.375,  adv=1.375-1=0.375
            "1, 0.875,-0.125,false",   //sumRewards=0+0.5+0.25, value(sFut) = 0.5^3*1=1/8 => value=0.75+1/8=0.875,  adv=0.875-1=-0.125
    })
    void givenStepHorizon1AndCriticValue1AndGamma0dot5_whenGenerating_thenCorrectValueAndAdvantage(ArgumentsAccessor arguments) {
        generator = createGenerator(3, 0.5);
        fitMemory(dependencies.agent(), 1.0);
        assertMultiStepResults(ArgumentAdaptor.of(arguments), generator.generate(experiences));
    }

    record ArgumentAdaptor(int step, double value, double advantage, boolean isFutOutSide) {
        public static ArgumentAdaptor of(ArgumentsAccessor arguments) {
            return new ArgumentAdaptor(
                    arguments.getInteger(0),
                    arguments.getDouble(1),
                    arguments.getDouble(2),
                    arguments.getBoolean(3)
            );
        }
    }


    private static void assertMultiStepResults(ArgumentAdaptor aa, MultiStepResults msr) {
        Assertions.assertEquals(aa.value, msr.valueTarAtStep(aa.step));
        Assertions.assertEquals(aa.advantage, msr.advantageAtStep(aa.step));
        Assertions.assertEquals(aa.isFutOutSide, msr.isFutureOutsideOrTerminalAtStep(aa.step));
    }


    private static ExperienceLunar expNotFail(int reward, boolean isTerminal) {
        StateLunar state = StateLunar.zeroPosAndSpeed();
        StateLunar stateNew = StateLunar.zeroPosAndSpeed();
        return ExperienceLunar.of(
                state, 0d, StepReturnLunar.ofNotFail(stateNew, isTerminal, reward));
    }


    private void fitMemory(AgentI agent, double vTarget) {
        for (int i = 0; i < N_EPOCHS; i++) {
            //StateLunar state = StateLunar.randomPosAndSpeed(dependencies.environment().getProperties());
            StateLunar state = StateLunar.zeroPosAndSpeed();
            for (int j = 0; j < N_FITS; j++) {
                double error = vTarget - agent.readCritic(state);
                agent.fitCritic(state, error);
            }
        }

    }


}
