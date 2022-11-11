package black_jack.models_episode;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedObserved;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class EpisodeItem {
    public StateObservedObserved state;
    public CardAction action;
    public Double reward;
}
