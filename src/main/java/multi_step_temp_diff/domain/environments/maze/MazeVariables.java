package multi_step_temp_diff.domain.environments.maze;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class MazeVariables {

    public int x,y,nofSteps;

    public static MazeVariables newFromXY(int x, int y) {
        return new MazeVariables(x,y,0);
    }
    public MazeVariables copy() {
        return  new MazeVariables(x,y,nofSteps);
    }

}
