package multi_step_temp_diff.environments;

public class ForkVariables {

    public int position;

    public ForkVariables(int position) {
        this.position = position;
    }

    public ForkVariables copy() {
        return new ForkVariables(position);
    }

}
