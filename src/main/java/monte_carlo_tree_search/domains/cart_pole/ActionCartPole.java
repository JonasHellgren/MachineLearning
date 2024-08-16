package monte_carlo_tree_search.domains.cart_pole;

import common.other.Conditionals;
import common.math.MathUtils;
import common.other.RandUtilsML;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.java.Log;
import monte_carlo_tree_search.interfaces.ActionInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
@Log
@ToString
public class ActionCartPole implements ActionInterface<Integer> {

    private static final int RAW_DEFAULT = 0;
    private static final int NON_APPLIC_VALUE = -1;
    private static final int NOF_LEVELS_DEFAULT = 2;
    @Builder.Default
    private Integer rawValue = RAW_DEFAULT;

    public static ActionCartPole newRandom() {
        ActionCartPole actionDummy=new ActionCartPole(0);
        Set<Integer> actions= actionDummy.applicableActions();
        List<Integer> actionlist=new ArrayList<>(actions);
        RandUtilsML<Integer> rand=new RandUtilsML<>();
        return ActionCartPole.builder().rawValue(rand.getRandomItemFromList(actionlist)).build();
    }

    @Override
    public void setValue(Integer actionValue) {
        Conditionals.executeIfTrue(actionValue < NON_APPLIC_VALUE || actionValue >= NOF_LEVELS_DEFAULT, () ->
                log.warning("Non valid value = "+actionValue+", nof levels = "+NOF_LEVELS_DEFAULT));

        this.rawValue = MathUtils.clip(actionValue, NON_APPLIC_VALUE, NOF_LEVELS_DEFAULT - 1);
    }

    @Override
    public Integer getValue() {
        return rawValue;
    }

    @Override
    public ActionInterface<Integer> copy() {
        return ActionCartPole.builder().rawValue(rawValue).build();
    }

    @Override
    public Set<Integer> applicableActions() {
        return IntStream.range(0, NOF_LEVELS_DEFAULT)  //exclusive end
                .boxed()
                .collect(Collectors.toSet());
    }

    @Override
    public Integer nonApplicableAction() {
        return NON_APPLIC_VALUE;
    }
}
