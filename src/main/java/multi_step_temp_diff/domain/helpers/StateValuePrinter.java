package multi_step_temp_diff.domain.helpers;

import common.MySetUtils;
import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import org.apache.commons.math3.util.Pair;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
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
        DecimalFormat formatter = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        BiFunction<AgentMazeNeural, Pair<Integer,Integer>,String> getValueForCell= (a,p) ->
                formatter.format(a.readValue(MazeState.newFromXY(p.getFirst(),p.getSecond() )));

        AgentMazeNeural agentMazeNeural=(AgentMazeNeural) agent;
        for (int y = MazeEnvironment.settings.nofRows()-1; y >=0 ; y--) {
            StringBuilder sb=new StringBuilder();
            //for (int y : SetUtils.getSetFromRange(0, MazeEnvironment.NOF_ROWS)) {
            for (int x : MySetUtils.getSetFromRange(0, MazeEnvironment.settings.nofCols())) {
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
