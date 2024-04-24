package safe_rl.helpers;

import common.other.Conditionals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;

class TestExperienceCreator {

    public static final double TARGET_MEAN = 2d;
    public static final double TARGET_STD = 2d;
    public static final double MIN_ACTION = 0;
    ExperienceCreator<VariablesBuying> experienceCreator;
    BuySettings settings3 = BuySettings.new3HoursSamePrice();
    EnvironmentBuying environment;
    AgentACDCSafeBuyer agent;
    SafetyLayer<VariablesBuying> safetyLayer;
    StateBuying state;

    @BeforeEach
    void init() {
        environment = new EnvironmentBuying(settings3);
        state = StateBuying.of(VariablesBuying.newSoc(0.5));
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3));
        var trainerParameters = TrainerParameters.newDefault();
        agent = AgentACDCSafeBuyer.builder()
                .targetMean(TARGET_MEAN).targetLogStd(Math.log(TARGET_STD))
                .settings(BuySettings.new3HoursSamePrice())
                .state(StateBuying.newZero())
                .build();
        experienceCreator = ExperienceCreator.<VariablesBuying>builder()
                .environment(environment).parameters(trainerParameters).safetyLayer(safetyLayer)
                .build();
    }

    @Test
    void whenGettingExperiences_thenCorrect() {
        var experiences = experienceCreator.getExperiences(agent,state);
        var ei=new EpisodeInfo<>(experiences);
        var minMax=ei.minMaxAppliedAction();
        System.out.println("minMax = " + minMax);

        experiences.forEach(System.out::println);


        experiences.forEach(e -> Conditionals.executeOneOfTwo(e.isSafeCorrected(),
                () -> {
                    Assertions.assertTrue(e.arsCorrected().isPresent());
                    Assertions.assertNotNull(e.arsCorrected().orElseThrow().stateNext());
                }
                , () -> {
                    Assertions.assertTrue(e.arsCorrected().isEmpty());
                    Assertions.assertNotNull(e.ars().stateNext());
                }));

        Assertions.assertEquals(ei.size(),ei.nCorrected()+ei.nNotCorrected());
        Assertions.assertEquals(ei.size(),ei.nZeroValued());
        Assertions.assertEquals(1,ei.nIsTerminal());
        Assertions.assertTrue(ei.minMaxAppliedAction().getFirst()> MIN_ACTION);
        Assertions.assertTrue(ei.minMaxAppliedAction().getSecond()<TARGET_MEAN+TARGET_STD*2);

    }


}
