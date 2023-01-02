package monte_carlo_tree_search.domains.models_space;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SpaceCell {

    public boolean isObstacle;
    public double bonus;
    public boolean isGoal;
    public boolean isOnLowerBorder;
    public boolean isOnUpperBorder;

    public static SpaceCell EMPTY() {
        return new SpaceCell(false,0,false,false,false);
    }

}
