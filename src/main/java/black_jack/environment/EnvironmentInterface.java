package black_jack.environment;

import black_jack.models.StateCards;
import black_jack.models.StateObserved;
import black_jack.models.StepReturnBJ;

public interface EnvironmentInterface {

    StepReturnBJ step(int action, StateCards state);


}
