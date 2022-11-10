package black_jack.environment;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateCards;

public interface EnvironmentInterface {

    StepReturnBJ step(CardAction action, StateCards state);


}
