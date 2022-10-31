package black_jack.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StepReturnBJ {

    public StateCards cards;
    public boolean stopPlaying;
    public double reward;
}
