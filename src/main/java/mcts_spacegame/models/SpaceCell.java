package mcts_spacegame.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class SpaceCell {

    public boolean isObstacle;
    public boolean isGoal;
    public boolean isOnLowerBorder;
    public boolean isOnUpperBorder;

    public static SpaceCell EMPTY() {
        return new SpaceCell(false,false,false,false);
    }

}
