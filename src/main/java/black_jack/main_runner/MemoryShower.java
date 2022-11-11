package black_jack.main_runner;

import black_jack.models_cards.StateObserved;
import black_jack.models_memory.MemoryInterface;
import black_jack.result_drawer.GridPanel;

import java.util.List;

public class MemoryShower<T> {

     void showValueMemory(GridPanel panelNoUsableAce, GridPanel panelUsableAce, MemoryInterface<T> stateValueMemory) {
        setPanel(panelNoUsableAce, stateValueMemory, false);
        setPanel(panelUsableAce, stateValueMemory, true);
        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

     void setPanel(GridPanel panel, MemoryInterface<T> stateValueMemory, boolean usableAce) {
        List<Integer> xSet = StateObserved.getDealerCardList();
        List<Integer> ySet = StateObserved.getHandsSumList();
        for (int y : ySet) {
            for (int x : xSet) {
                StateObserved s=new StateObserved(y, usableAce, x);
                double value = stateValueMemory.readBestValue(s);
                panel.setNumbersAtCell(x,y, value);
            }
        }
        panel.setColorsAtCells();
        panel.setTextCellValues(true);
    }

}
