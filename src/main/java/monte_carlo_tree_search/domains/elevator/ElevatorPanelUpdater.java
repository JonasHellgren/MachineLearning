package monte_carlo_tree_search.domains.elevator;

import black_jack.result_drawer.GridPanel;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import java.util.Optional;

public class ElevatorPanelUpdater {

    private static final double WHITE_COLOR = 1;
    private static final double BLACK_COLOR = 0;
    StateInterface<VariablesElevator> state;
    GridPanel panel;

    public ElevatorPanelUpdater(StateInterface<VariablesElevator> state,
                                GridPanel panel) {
        this.state = state;
        this.panel = panel;
    }

    public void insertStates() {
        panel.setTextCellValues(false);
        setElevatorPos();
        setWaitingPersons();
        panel.repaint();
    }

     void setElevatorPos() {
        VariablesElevator variables=state.getVariables();
         for (int pos = 0; pos <= EnvironmentElevator.MAX_POS ; pos++) {
             double value=(variables.pos==pos)? WHITE_COLOR : BLACK_COLOR;
             panel.setNumbersAtCell(0,pos, value);
         }
        panel.setColorsAtCells();

     }

    void setWaitingPersons() {
        VariablesElevator variables=state.getVariables();
        for (int pos = 0; pos <= EnvironmentElevator.MAX_POS ; pos++) {
            Optional<Integer> floor= EnvironmentElevator.getFloor(pos);
            if (isPositionAtNonBottomFloor(pos)) {
                double value = (variables.nPersonsWaiting.get(floor.orElseThrow()-1) > 0) ? WHITE_COLOR : BLACK_COLOR;
                panel.setNumbersAtCell(1,pos, value);
            }
        }
        panel.setColorsAtCells();
    }

    private boolean isPositionAtNonBottomFloor(int pos) {
        Optional<Integer> floor= EnvironmentElevator.getFloor(pos);
        return floor.isPresent() && floor.get() != 0;
    }


}
