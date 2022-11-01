package black_jack.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class StepReturnBJ {

    public StateCards cards;
    public boolean stopPlaying;
    public double reward;
}
