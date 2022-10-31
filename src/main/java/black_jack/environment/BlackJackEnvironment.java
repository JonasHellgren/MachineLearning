package black_jack.environment;

import black_jack.models.Card;
import black_jack.models.CardAction;
import black_jack.models.StateCards;
import black_jack.models.StepReturnBJ;

import java.util.List;
import java.util.stream.Collectors;

public class BlackJackEnvironment implements EnvironmentInterface {

    public static final int MAX_SUM_DEALER_FOR_HITTING = 17;

    @Override
    public StepReturnBJ step(CardAction action, StateCards state0) {

        StateCards state=StateCards.clone(state0);
        StepReturnBJ stepReturnBJ;

        if (action.equals(CardAction.hit))  {

            addRandomPlayerCard(state);
            final boolean stopPlayingIfBust = true;
            final boolean stopPlayingIfNotBust = true;
            final double rewardBust=-1;
            final double rewardStickButNotBust=0;

            stepReturnBJ=(isPlayerBust(state))
                    ? new StepReturnBJ(state,stopPlayingIfBust,rewardBust)
                    : new StepReturnBJ(state,stopPlayingIfNotBust,rewardStickButNotBust);


        } else
        {
            final boolean stopPlaying = true;

            while (state.sumHandDealer()< MAX_SUM_DEALER_FOR_HITTING) {
                state.addDealerCard(Card.newRandom());
            }

            boolean playerHasBetterHand=scoreHandPlayer(state)>scoreHandDealer(state);
            boolean hasAceAndFacedCard=isAceAndFacedCard(state.getCardsPlayer());

            double reward=(!playerHasBetterHand)
                    ? -1
                    : (hasAceAndFacedCard)  ? 1.5 : 1;

            stepReturnBJ=new StepReturnBJ(state,stopPlaying,reward);

        }


        return stepReturnBJ;
    }


    private void addRandomPlayerCard(StateCards cards) {
        cards.addPlayerCard(Card.newRandom());
    }

    private boolean isPlayerBust(StateCards state)  {
        return state.sumHandPlayer()>21;
    }

    private boolean isDealerBust(StateCards state)  {
        return state.sumHandDealer()>21;
    }

    public long scoreHandPlayer(StateCards state) {
        return (isPlayerBust(state))
        ? 0
        :state.sumHandPlayer();
    }

    public long scoreHandDealer(StateCards state) {
        return (isDealerBust(state))
                ? 0
                :state.sumHandDealer();
    }

    boolean isAceAndFacedCard(List<Card> cards) {
        List<Long> cardValues= cards.stream().map(c -> c.getValue()).collect(Collectors.toList());
        return  cardValues.contains(1L) && cardValues.contains(10L);

    }

}
