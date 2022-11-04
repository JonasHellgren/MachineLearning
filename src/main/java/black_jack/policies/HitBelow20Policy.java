package black_jack.policies;

import black_jack.helper.CardsInfo;
import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StateObserved;

public class HitBelow20Policy implements PolicyInterface {

    @Override
    public  CardAction hitOrStick(StateObserved observed) {
        double score = CardsInfo.scoreHandPlayer(observed);
        return (score < 20)
                ? CardAction.hit
                : CardAction.stick;
    }

}
