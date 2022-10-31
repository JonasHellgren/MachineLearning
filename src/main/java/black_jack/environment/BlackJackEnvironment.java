package black_jack.environment;

import black_jack.helper.CardsInfo;
import black_jack.models.Card;
import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StepReturnBJ;

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
            stepReturnBJ=(CardsInfo.isPlayerBust(state))
                    ? new StepReturnBJ(state, STOP_PLAYING_IF_BUST, REWARD_BUST)
                    : new StepReturnBJ(state, STOP_PLAYING_IF_NOT_BUST, REWARD_STICK_BUT_NOT_BUST);
        } else
        {
            addDealerCardsUntilMaxSum(state);
            boolean playerHasBetterHand= CardsInfo.scoreHandPlayer(state)> CardsInfo.scoreHandDealer(state);
            boolean playerHasAceAndFacedCard= CardsInfo.isAceAndFacedCardPresent(state.getCardsPlayer());
            double reward = getRewardStick(playerHasBetterHand, playerHasAceAndFacedCard);
            stepReturnBJ=new StepReturnBJ(state, STOP_PLAYING,reward);

        }
        return stepReturnBJ;
    }

    private void addDealerCardsUntilMaxSum(StateCards state) {
        while (CardsInfo.sumHandPlayer(state)< MAX_SUM_DEALER_FOR_HITTING) {
            state.addDealerCard(Card.newRandom());
        }
    }

    private double getRewardStick(boolean playerHasBetterHand, boolean hasAceAndFacedCard) {
        return (!playerHasBetterHand)
                ? REWARD_DEALER_HAS_BETTER_HAND
                : hasAceAndFacedCard ? REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD : REWARD_PLAYER_BETTER_HAND;
    }


}
