package black_jack.environment;

import black_jack.helper.CardsHelper;
import black_jack.models.Card;
import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StepReturnBJ;

import java.util.List;
import java.util.stream.Collectors;

public class BlackJackEnvironment implements EnvironmentInterface {

    public static final int MAX_SUM_DEALER_FOR_HITTING = 17;
    public static final boolean STOP_PLAYING_IF_BUST = true;
    public static final boolean STOP_PLAYING_IF_NOT_BUST = false;
    public static final boolean STOP_PLAYING = true;
    public static final int REWARD_BUST = -1;
    public static final int REWARD_STICK_BUT_NOT_BUST = 0;
    public static final int REWARD_DEALER_HAS_BETTER_HAND = -1;
    public static final double REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD = 1.5;
    public static final int REWARD_PLAYER_BETTER_HAND = 1;

    @Override
    public StepReturnBJ step(CardAction action, StateCards state0) {

        StateCards state=StateCards.clone(state0);
        StepReturnBJ stepReturnBJ;

        if (action.equals(CardAction.hit))  {
            state.addPlayerCard(Card.newRandom());
            stepReturnBJ=(CardsHelper.isPlayerBust(state))
                    ? new StepReturnBJ(state, STOP_PLAYING_IF_BUST, REWARD_BUST)
                    : new StepReturnBJ(state, STOP_PLAYING_IF_NOT_BUST, REWARD_STICK_BUT_NOT_BUST);
        } else
        {
            addDealerCardsUntilMaxSum(state);
            boolean playerHasBetterHand=CardsHelper.scoreHandPlayer(state)>CardsHelper.scoreHandDealer(state);
            boolean hasAceAndFacedCard=CardsHelper.isAceAndFacedCard(state.getCardsPlayer());
            double reward = getReward(playerHasBetterHand, hasAceAndFacedCard);
            stepReturnBJ=new StepReturnBJ(state, STOP_PLAYING,reward);

        }
        return stepReturnBJ;
    }

    private void addDealerCardsUntilMaxSum(StateCards state) {
        while (CardsHelper.sumHandPlayer(state)< MAX_SUM_DEALER_FOR_HITTING) {
            state.addDealerCard(Card.newRandom());
        }
    }

    private double getReward(boolean playerHasBetterHand, boolean hasAceAndFacedCard) {
        return (!playerHasBetterHand)
                ? REWARD_DEALER_HAS_BETTER_HAND
                : hasAceAndFacedCard ? REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD : REWARD_PLAYER_BETTER_HAND;
    }


}
