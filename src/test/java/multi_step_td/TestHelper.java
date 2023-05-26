package multi_step_td;

import multi_step_temp_diff.interfaces.NetworkMemoryInterface;
import multi_step_temp_diff.models.ForkEnvironment;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public class TestHelper {

    public static void printStateValues(NetworkMemoryInterface<Integer> memory) {
        Map<Integer,Double> stateValues=new HashMap<>();
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            stateValues.put(si,memory.read(si));
        }
        System.out.println("stateValues = " + stateValues);
    }

    public static void assertAllStates(NetworkMemoryInterface<Integer> memory,
                                       double value, double delta) {
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            Assert.assertEquals(value, memory.read(si), delta);
        }
    }

}
