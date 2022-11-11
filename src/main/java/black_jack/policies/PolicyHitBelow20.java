package black_jack.policies;

import black_jack.helper.CardsInfo;
import black_jack.enums.CardAction;
import black_jack.models_cards.StateObserved;

public class PolicyHitBelow20 implements PolicyInterface {

    @Override
    public  CardAction hitOrStick(StateObserved observed) {
        double score = CardsInfo.scoreHandPlayer(observed);
        return (score < 20)
                ? CardAction.hit
                : CardAction.stick;
    }

}
