package black_jack.environment;

import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StateObserved;
import black_jack.models.StepReturnBJ;

public interface EnvironmentInterface {

    StepReturnBJ step(CardAction action, StateCards state);


}
