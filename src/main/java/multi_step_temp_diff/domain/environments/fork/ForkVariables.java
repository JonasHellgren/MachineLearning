package multi_step_temp_diff.domain.environments.fork;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
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
