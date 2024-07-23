package maze_domain_design.domain.environment.value_objects;

import org.apache.commons.lang3.RandomUtils;

public enum Action {
    N(1,"↑"), E(0,"→"), S(-1,"↓");

    public final int deltaY;
    public final String arrow;

    Action(int deltaY,String arrow) {
        this.deltaY = deltaY;
        this.arrow=arrow;
    }

    public static Action random() {
        int randIdx= RandomUtils.nextInt(0,Action.values().length);
        return Action.values()[randIdx];
    }

    public boolean isMove() {
        return this.equals(N) || this.equals(S);
    }


}
