package black_jack.policies;

import black_jack.models.CardAction;
import black_jack.models.StateObserved;

public interface PolicyInterface {

    CardAction hitOrStick(StateObserved observed);

}
