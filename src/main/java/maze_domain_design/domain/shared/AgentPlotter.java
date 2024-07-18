package maze_domain_design.domain.shared;

import common.plotters.SwingShowHeatMap;
import lombok.AllArgsConstructor;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.State;

@AllArgsConstructor
public class AgentPlotter {

    Agent agent;
    Environment environment;

    public void plot() {
        var ep = environment.getProperties();
        int nX = ep.minMaxX().getSecond() - ep.minMaxX().getFirst() + 1;
        int nY = ep.minMaxY().getSecond() - ep.minMaxY().getFirst() + 1;
        Double[][] values=new Double[nY][nX];
        String[][] actions=new String[nY][nX];
        for (int y = ep.minMaxY().getFirst(); y <= ep.minMaxY().getSecond(); y++) {
            for (int x = ep.minMaxX().getFirst(); x <= ep.minMaxX().getSecond(); x++) {
                State s = State.of(x, y, ep);
                if (!s.isTerminal()) {
                    values[y][x] = agent.getMemory().valueOfState(s);
                    actions[y][x] = agent.chooseAction(s, 0d).arrow;
                }
               }
        }
        var showerV= SwingShowHeatMap.builder()
                //.xLabels(new String[]{"X1", "X2", "X3"})
                //.yLabels(new String[]{"Y1", "Y2", "Y3"})
                .isLabels(false)
                .build();
        showerV.showMap(values,"V");

        var showerA= SwingShowHeatMap.builder()
                //.xLabels(new String[]{"X1", "X2", "X3"})
                //.yLabels(new String[]{"Y1", "Y2", "Y3"})
                //.width(200).height(150)
                .isLabels(false)
                .build();
        showerA.showMap(actions,"A");
    }
}
