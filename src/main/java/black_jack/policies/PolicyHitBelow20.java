package black_jack.policies;

import black_jack.helper.CardsInfo;
import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedObserved;

public class PolicyHitBelow20 implements PolicyInterface {

    @Override
    public  CardAction hitOrStick(StateObservedObserved observed) {
        double score = CardsInfo.scoreHandPlayer(observed);
        return (score < 20)
                ? CardAction.hit
                : CardAction.stick;
    }

}
