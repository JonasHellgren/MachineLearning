package qlearning_objoriented_class_sceleton;

import org.junit.Assert;
import org.junit.Test;
import qlearning_objoriented_class_sceleton.models.State;

public class TestStateRoad {
    State state = new State();

    @Test
    public void createVariable() {
        state.createVariable("roomNumber", 1);
        Assert.assertEquals(state.getDiscreteVariable("roomNumber"), 1, 0.01);
    }

    @Test
    public void setVariable() {
        state.createVariable("roomNumber", 1);
        state.setVariable("roomNumber", 10);
        Assert.assertEquals(state.getDiscreteVariable("roomNumber"), 10, 0.01);
    }

    @Test
    public void createManyContinuousVariables() {
        state.createVariable("speed", 1d);
        state.createVariable("velocity", 1.2d);

        System.out.println(state);
    }

}
