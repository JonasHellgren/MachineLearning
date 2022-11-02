package black_jack.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class EpisodeItem {
    public StateObserved state;
    public CardAction action;
    public Double reward;
}
