package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.domain.agent.*;
import book_rl_explained.lunar_lander.domain.environment.*;
import book_rl_explained.lunar_lander.domain.trainer.*;
import org.junit.jupiter.api.BeforeEach;

public class TestEpisodeCreator {

    EpisodeCreator creator;

     @BeforeEach
      void init() {

         var ep = LunarProperties.defaultProps();
         var p=AgentParameters.defaultProps(ep);
         var trainerDependencies = TrainerDependencies.builder()
                 .agent(AgentLunar.zeroWeights(p,ep))
                 .environment(EnvironmentLunar.createDefault())
                 .trainerParameters(TrainerParameters.defaultParams())
                 //.startStateSupplier(StateLunar::randomPosAndZeroSpeed)
                 .build();
         creator = new EpisodeCreator(trainerDependencies);



      }
}
