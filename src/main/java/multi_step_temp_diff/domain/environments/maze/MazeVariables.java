package multi_step_temp_diff.domain.environments.maze;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class MazeVariables {

    public int x,y;
    public static MazeVariables newFromXY(int x, int y) {
        return new MazeVariables(x,y);
    }
    public MazeVariables copy() {
        return  MazeVariables.newFromXY(x,y);
    }

}
