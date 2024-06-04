package multi_agent_rl.environments.apple;

import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import common.other.RandUtils;
import multi_agent_rl.domain.abstract_classes.ActionAgent;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.StateI;

import java.util.List;
import java.util.stream.IntStream;
import static common.list_arrays.IndexFinder.findBucket;
import static common.math.BucketLimitsHandler.getLimits;
import static common.math.BucketLimitsHandler.throwIfBadLimits;

public class AgentApple<V> implements AgentI<V> {

    //StateI<V> state;

    @Override
    public ActionAgent chooseAction(StateI<V> state) {
        var limits = getLimits(actionProbabilitiesInPresentState());
        throwIfBadLimits(limits);
        return ActionAgent.ofInteger(findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne()));
    }

    public List<Double> actionProbabilitiesInPresentState() {
        double[] values= IntStream.range(0,ActionAppleRobot.nActions()).mapToDouble(i -> RandUtils.randomNumberBetweenZeroAndOne()).toArray();
        double sumValues= ArrayUtil.sum(values);
        List<Double> probs=ListUtils.arrayPrimitiveDoublesToList(values);
        return ListUtils.multiplyListElements(probs,1/sumValues);
    }

}
