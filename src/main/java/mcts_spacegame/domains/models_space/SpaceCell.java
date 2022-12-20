package mcts_spacegame.domains.models_space;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SpaceCell {

    public boolean isObstacle;
    public boolean isGoal;
    public boolean isOnLowerBorder;
    public boolean isOnUpperBorder;

    public static SpaceCell EMPTY() {
        return new SpaceCell(false,false,false,false);
    }

}
