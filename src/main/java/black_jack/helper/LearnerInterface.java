package black_jack.helper;

import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;

public interface LearnerInterface {

     double ALPHA_DEFAULT = 0.1;
     boolean FLAG_DEFAULT=false;

    void updateMemoryFromEpisodeReturns(ReturnsForEpisode returns);
    void updateMemory(ReturnItem ri);

}
