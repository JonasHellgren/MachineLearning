package multi_step_temp_diff.environments;

import lombok.ToString;

@ToString
public class ForkVariables {

    public int position;

    public ForkVariables(int position) {
        this.position = position;
    }

    public static ForkVariables newFromPos(int position) {
        return new ForkVariables(position);
    }

    public ForkVariables copy() {
        return new ForkVariables(position);
    }

}
