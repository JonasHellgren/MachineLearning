package multi_agent_rl.environments.apple;

import multi_agent_rl.domain.abstract_classes.Action;
import multi_agent_rl.domain.abstract_classes.EnvironmentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.StepReturn;

import java.util.List;

public class EnvironmentApple implements EnvironmentI<VariablesApple> {


    @Override
    public StepReturn<VariablesApple> step(StateI<VariablesApple> state, Action action) {

        List<Integer> actionList=action.asInts();
        ActionRobot actionRobotA = ActionRobot.fromInt(actionList.get(0));
        ActionRobot actionRobotB = ActionRobot.fromInt(actionList.get(1));
        

        return null;
    }
}
