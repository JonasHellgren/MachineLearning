package black_jack.policies;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObserved;

import java.util.Random;

public class PolicyRandom implements PolicyInterface {

    Random random;

    public PolicyRandom() {
        this.random = new Random();
    }

    @Override
    public CardAction hitOrStick(StateObserved observed) {
        return (random.nextInt(2)==0)
                ?CardAction.stick
                :CardAction.hit;
    }
}
