package black_jack.environment;

import black_jack.helper.CardsInfo;
import black_jack.models.Card;
import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StepReturnBJ;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BlackJackEnvironment implements EnvironmentInterface {

    public static final int MAX_SUM_DEALER_FOR_HITTING = 17;
    public static final boolean STOP_PLAYING = true;
    public static final int REWARD_BUST = -1;
    public static final int REWARD_STICK_BUT_NOT_BUST = 0;
    public static final int REWARD_DEALER_HAS_BETTER_HAND = -1;
    public double REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD = 1.5;
    public static final int REWARD_PLAYER_BETTER_HAND = 1;

    public static BlackJackEnvironment newHigherRewardIfAceAndFaceCard() {
        return new BlackJackEnvironment(1.5);
    }

    public static BlackJackEnvironment newSameRewardIfAceAndFaceCard() {
        return new BlackJackEnvironment(1.0);
    }

    private BlackJackEnvironment(double reward) {
        this.REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD = reward;
    }

    @Override
    public StepReturnBJ step(CardAction action, StateCards state0) {

        StateCards state=StateCards.clone(state0);
        StepReturnBJ stepReturnBJ;

        if (action.equals(CardAction.hit))  {
            state.addPlayerCard(Card.newRandom());
            stepReturnBJ=(CardsInfo.isPlayerBust(state))
                    ? new StepReturnBJ(state, STOP_PLAYING, REWARD_BUST)
                    : new StepReturnBJ(state, !STOP_PLAYING, REWARD_STICK_BUT_NOT_BUST);  //continue playing
        } else
        {
            addDealerCardsUntilMaxSum(state);
            boolean playerHasBetterHand= CardsInfo.scoreHandPlayer(state)> CardsInfo.scoreHandDealer(state);
            boolean playerHasAceAndFacedCard= CardsInfo.isAceAndFacedCardPresent(state.getCardsPlayer());
            double reward = getRewardStick(playerHasBetterHand, playerHasAceAndFacedCard);
            stepReturnBJ=new StepReturnBJ(state, STOP_PLAYING,reward);

          //  boolean isDealerBust=CardsInfo.isDealerBust(state);
          //  System.out.println("state = " + state+", playerHasBetterHand = " + playerHasBetterHand+", isDealerBust="+isDealerBust);
        }
        return stepReturnBJ;
    }

    private void addDealerCardsUntilMaxSum(StateCards state) {
        while (CardsInfo.sumHandDealer(state) < MAX_SUM_DEALER_FOR_HITTING) {
            state.addDealerCard(Card.newRandom());  //add until sum is 17 or greater
        }
    }

    private double getRewardStick(boolean playerHasBetterHand, boolean hasAceAndFacedCard) {
        return (!playerHasBetterHand)
                ? REWARD_DEALER_HAS_BETTER_HAND
                : hasAceAndFacedCard ? REWARD_PLAYER_BETTER_HAND_AND_ACE_AND_FACE_CARD : REWARD_PLAYER_BETTER_HAND;
    }


}
