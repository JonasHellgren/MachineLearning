package safe_rl.domain.helpers;

import common.other.Conditionals;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.aggregates.EpisodeCreator;
import safe_rl.domain.trainer.helpers.EpisodeInfo;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;

class TestEpisodeCreator {

    public static final double TARGET_MEAN = 2d;
    public static final double TARGET_STD = 2d;
    public static final double MIN_ACTION = 0;
    EpisodeCreator<VariablesBuying> episodeCreator;
    SettingsBuying settings3 = SettingsBuying.new3HoursSamePrice();
    EnvironmentBuying environment;
    AgentACDCSafe<VariablesBuying> agent;
    SafetyLayer<VariablesBuying> safetyLayer;
    StateBuying state;

    @BeforeEach
    void init() {
        environment = new EnvironmentBuying(settings3);
        state = StateBuying.of(VariablesBuying.newSoc(0.5));
        safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3));
        var trainerParameters = TrainerParameters.newDefault();
        agent = AgentACDCSafe.<VariablesBuying>builder()
                //.trainerParameters(TrainerParameters.newDefault())
                .parameters(AgentParameters.newDefault()
                        .withTargetMean(TARGET_MEAN).withTargetLogStd(Math.log(TARGET_STD)))
                .settings(SettingsBuying.new3HoursSamePrice())
                .state(StateBuying.newZero())
                .build();
      /*  episodeCreator = EpisodeCreator.<VariablesBuying>builder()
                .environment(environment).parameters(trainerParameters).safetyLayer(safetyLayer)
                .build();
*/    }

    @Test
    @SneakyThrows
    void whenGettingExperiences_thenCorrect() {
        var experiences = episodeCreator.getExperiences(agent, state);
        var ei = new EpisodeInfo<>(experiences);
        var minMax = ei.minMaxAppliedAction();
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

        Assertions.assertEquals(ei.size(), ei.nCorrected() + ei.nNotCorrected());
        Assertions.assertEquals(ei.size(), ei.nZeroValued());
        Assertions.assertEquals(1, ei.nIsTerminal());
        Assertions.assertTrue(ei.minMaxAppliedAction().getFirst() > MIN_ACTION);
        Assertions.assertTrue(ei.minMaxAppliedAction().getSecond() < TARGET_MEAN + TARGET_STD * 2);

    }


}
