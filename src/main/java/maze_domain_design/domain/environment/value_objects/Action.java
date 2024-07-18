package maze_domain_design.domain.environment.value_objects;

import org.apache.commons.lang3.RandomUtils;

public enum Action {
    UP(1,"↑"), SAME(0,"→"), DOWN(-1,"↓");

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
        return this.equals(UP) || this.equals(DOWN);
    }
}
