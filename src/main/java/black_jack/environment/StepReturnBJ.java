package black_jack.environment;

import black_jack.models_cards.StateCards;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class StepReturnBJ {

    public StateCards cards;
    public boolean stopPlaying;
    public double reward;
}
