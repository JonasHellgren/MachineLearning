package multi_step_td;

import common.ListUtils;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;
import multi_step_temp_diff.environments.ForkEnvironment;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHelper {

    public static void printStateValues(NetworkMemoryInterface<Integer> memory) {
        Map<Integer,Double> stateValues=new HashMap<>();
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            stateValues.put(si,memory.read(si));
        }
        ;
        stateValues.forEach((s,v) -> System.out.println("s="+s+", v="+v));
    }

    public static void assertAllStates(NetworkMemoryInterface<Integer> memory,
                                       double value, double delta) {
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            Assert.assertEquals(value, memory.read(si), delta);
        }
    }

    public static double avgError(Map<Integer, Double> mapOneStep) {
        List<Double> errors=new ArrayList<>();
        errors.add(Math.abs(mapOneStep.get(0)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(mapOneStep.get(7)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(mapOneStep.get(6)-ForkEnvironment.R_HELL));
        errors.add(Math.abs(mapOneStep.get(11)-ForkEnvironment.R_HELL));
        return ListUtils.findAverage(errors).orElseThrow();
    }

}
