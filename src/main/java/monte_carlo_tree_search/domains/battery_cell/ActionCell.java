package monte_carlo_tree_search.domains.battery_cell;

import common.other.Conditionals;
import common.math.MathUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import monte_carlo_tree_search.interfaces.ActionInterface;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 *  This class defines action for a batter cell, it is a discrete value currentLevel
 *
 * The constructor if designed so an exception occurs if nofCurrentLevels <= 1.
 * nofCurrentLevels=3, minRelativeCurrent=0.5, maxRelativeCurrent=1
 * level=0 => relCurrent=0.5, level=1 => relCurrent=0.75, level=2 => relCurrent=1.0
 */

@Getter
@Builder
@Log
@ToString(includeFieldNames = false)
public class ActionCell implements ActionInterface<Integer> {
    private static final int NOF_LEVELS_DEFAULT = 3;
    private static final double MAX_RELATIVE_CURRENT_DEFAULT = 1;
    private static final double MIN_RELATIVE_CURRENT_DEFAULT = 0.5;
    private static final int LEVEL_DEFAULT = 0;
    private static final int NON_APPLIC_VALUE = -1;
    //@Builder.Default  does not work with custom builder
    @NonNull
    @ToString.Exclude
    private final Integer nofCurrentLevels;
    @Builder.Default
    @ToString.Exclude
    private final double maxRelativeCurrent = MAX_RELATIVE_CURRENT_DEFAULT;
    @Builder.Default
    @ToString.Exclude
    private final double minRelativeCurrent = MIN_RELATIVE_CURRENT_DEFAULT;
    @Builder.Default
    private Integer currentLevel = LEVEL_DEFAULT;


    //http://www.devnips.com/2021/05/adding-custom-validation-in-lombok.html
    public static ActionCellBuilder builder() {
        return new CustomBuilder();
    }

    public static class CustomBuilder extends ActionCellBuilder {
        public ActionCell build() {
        if (super.nofCurrentLevels <= 1) {
            throw new IllegalArgumentException("Non valid nof current levels");
        }
            return super.build();
        }
    }

    public static ActionCell newDefault() {
        return ActionCell.builder().nofCurrentLevels(NOF_LEVELS_DEFAULT).build();
    }

    @Override
   public void setValue(Integer level) {
        Conditionals.executeIfTrue(level < NON_APPLIC_VALUE || level >= nofCurrentLevels, () ->
                log.warning("Non valid level = "+currentLevel+", nofCurrentLevels = "+nofCurrentLevels));

        this.currentLevel = MathUtils.clip(level, NON_APPLIC_VALUE, nofCurrentLevels - 1);
    }

    @Override
    public Integer getValue() {
        return currentLevel;
    }

    @Override
    public ActionInterface<Integer> copy() {
        return ActionCell.builder()
                .nofCurrentLevels(nofCurrentLevels)
                .maxRelativeCurrent(maxRelativeCurrent)
                .minRelativeCurrent(minRelativeCurrent)
                .currentLevel(currentLevel)
                .build();
    }

    @Override
    public Set<Integer> applicableActions() {
        return IntStream.range(0, nofCurrentLevels)  //exclusive end
                .boxed()
                .collect(Collectors.toSet());
    }

    @Override
    public Integer nonApplicableAction() {
        return NON_APPLIC_VALUE;
    }

    public double getRelativeCurrent() {
        double span=maxRelativeCurrent-minRelativeCurrent;
        return (nofCurrentLevels == 1)
                ? minRelativeCurrent
                : minRelativeCurrent + span*(double) currentLevel / (double) (nofCurrentLevels - 1);
    }

}
