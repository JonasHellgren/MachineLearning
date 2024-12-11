package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.domain.agent.*;
import book_rl_explained.lunar_lander.domain.environment.*;
import book_rl_explained.lunar_lander.domain.trainer.*;
import book_rl_explained.lunar_lander.helpers.ExperiencesInfo;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestEpisodeCreator {

    EpisodeCreator creator;

    @BeforeEach
    void init() {

        var ep = LunarProperties.defaultProps();
        var p = AgentParameters.defaultParams(ep);
        var trainerDependencies = TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.createDefault())
                .trainerParameters(TrainerParameters.defaultParams())
                .startStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep))
                .build();
        creator = new EpisodeCreator(trainerDependencies);
    }


    @Test
    void givenNonTrainedAgent_thenWillFail() {
        var experiences = creator.getExperiences();
        var info = ExperiencesInfo.of(experiences);


        System.out.println("info.endExperience() = " + info.endExperience());

        Assertions.assertTrue(info.endExperience().isFail());
    }
}
