package multi_step_temp_diff.helpers;

import common.SetUtils;
import lombok.AllArgsConstructor;
import multi_step_temp_diff.agents.AgentMazeNeural;
import multi_step_temp_diff.environments.MazeEnvironment;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import org.apache.commons.math3.util.Pair;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
public class StateValuePrinter<S> {

    AgentInterface<S> agent;
    EnvironmentInterface<S> environment;

    public void printMazeNeuralAgent()  {
        final String OBSTACLE = "X   ",GOAL = "G   ";
        Function<Pair<Integer,Integer>,String> getObjectForCell=(p)->
                MazeEnvironment.isObstacle.test(p.getFirst(),p.getSecond())? OBSTACLE : GOAL;
        BiFunction<AgentMazeNeural, Pair<Integer,Integer>,String> getValueForCell= (a,p) ->
                String.format("%.1f", a.readValue(MazeState.newFromXY(p.getFirst(),p.getSecond() )));

        AgentMazeNeural agentMazeNeural=(AgentMazeNeural) agent;
        for (int y = MazeEnvironment.NOF_ROWS-1; y >=0 ; y--) {
            StringBuilder sb=new StringBuilder();
            //for (int y : SetUtils.getSetFromRange(0, MazeEnvironment.NOF_ROWS)) {
            for (int x : SetUtils.getSetFromRange(0, MazeEnvironment.NOF_COLS)) {
                String str=MazeEnvironment.isObstacle.or(MazeEnvironment.isGoal).test(x,y)
                        ? getObjectForCell.apply(new Pair<>(x,y))
                        : getValueForCell.apply(agentMazeNeural, new Pair<>(x,y));
                sb.append(str).append("  |");
            }
            sb.append(System.lineSeparator());
            sb.append("-----------------------------------");
            System.out.println(sb);
        }

    }

}
