package mcts_spacegame.models_battery_cell;

import common.Conditionals;
import common.MathUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;

/***
 *  nofCurrentLevels=3, minRelativeCurrent=0.5, maxRelativeCurrent=1
 *  level=0 => relCurrent=0.5, level=1 => relCurrent=0.75, level=2 => relCurrent=1.0
 */

@Getter
@Builder
@Log
public class ActionCell implements ActionInterface {
    //  max(1f), moderate(0.8), low(0.5);

    private static final int NOF_LEVELS_DEFAULT = 3;
    private static final double MAX_RELATIVE_CURRENT_DEFAULT = 1;
    private static final double MIN_RELATIVE_CURRENT_DEFAULT = 0.5;
    private static final int LEVEL_DEFAULT = 0;
    @Builder.Default
    private final int nofCurrentLevels = NOF_LEVELS_DEFAULT;
    @Builder.Default
    private final double maxRelativeCurrent = MAX_RELATIVE_CURRENT_DEFAULT;
    @Builder.Default
    private final double minRelativeCurrent = MIN_RELATIVE_CURRENT_DEFAULT;
    @Builder.Default
    private int level = LEVEL_DEFAULT;

    public void setLevel(int level) {
        Conditionals.executeIfTrue(level < 0 || level >= nofCurrentLevels, () ->
                log.warning("Non valid level"));

        this.level = MathUtils.clip(level, 0, NOF_LEVELS_DEFAULT - 1);
    }

    public double getRelativeCurrent() {
        Conditionals.executeIfTrue(nofCurrentLevels <= 1, () ->
                log.warning("Nof levels must exceed 1"));
        return (nofCurrentLevels == 1)
                ? minRelativeCurrent
                : minRelativeCurrent + (double) level / (double) (nofCurrentLevels - 1);
    }

}
