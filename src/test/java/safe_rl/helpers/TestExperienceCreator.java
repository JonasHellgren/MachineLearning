package safe_rl.helpers;

import common.other.Conditionals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.trainers.ExperienceCreator;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;

class TestExperienceCreator {

    ExperienceCreator<VariablesBuying> experienceCreator;
    BuySettings settings3 = BuySettings.new3HoursSamePrice();
    EnvironmentBuying environment;
    AgentACDCSafeBuyer agent;
    SafetyLayerBuying<VariablesBuying> safetyLayer;
    StateBuying state;

    @BeforeEach
    void init() {
        environment = new EnvironmentBuying(settings3);
        state = StateBuying.of(VariablesBuying.newSoc(0.5));
        safetyLayer = new SafetyLayerBuying<>(settings3);
        var trainerParameters = TrainerParameters.newDefault();
        agent = AgentACDCSafeBuyer.builder()
                .targetMean(2d).targetStd(2d).settings(BuySettings.new3HoursSamePrice())
                .state(StateBuying.newZero())
                .build();
        experienceCreator = ExperienceCreator.<VariablesBuying>builder()
                .environment(environment).parameters(trainerParameters).safetyLayer(safetyLayer)
                .build();
    }

    @Test
    void whenGettingExperiences_thenCorrect() {
        var experiences = experienceCreator.getExperiences(agent);
        experiences.forEach(e -> Conditionals.executeOneOfTwo(e.isSafeCorrected(),
                () -> {
                    Assertions.assertTrue(e.arsCorrected().isPresent());
                    Assertions.assertNotNull(e.arsCorrected().orElseThrow().stateNext());
                }
                , () -> {
                    Assertions.assertTrue(e.arsCorrected().isEmpty());
                    Assertions.assertNotNull(e.ars().stateNext());
                }));
    }


}
