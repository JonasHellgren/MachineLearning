package multi_agent_rl.environments.apple;

import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import common.other.RandUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import multi_agent_rl.domain.abstract_classes.ActionAgent;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.ObservationI;
import java.util.List;
import java.util.stream.IntStream;
import static common.list_arrays.IndexFinder.findBucket;
import static common.math.BucketLimitsHandler.getLimits;
import static common.math.BucketLimitsHandler.throwIfBadLimits;

@AllArgsConstructor
public class AgentILApple<O> implements AgentI<O> {
    @Getter
    String id;

    @Override
    public ActionAgent chooseAction(ObservationI<O> obs) {
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

    public  double criticOut(ObservationI<O> obs) {
        return 0;
    }

}
