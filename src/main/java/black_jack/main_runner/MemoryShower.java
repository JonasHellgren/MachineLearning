package black_jack.main_runner;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateInterface;
import black_jack.models_cards.StateObserved;
import black_jack.models_cards.StateObservedAction;
import black_jack.models_memory.MemoryInterface;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.result_drawer.GridPanel;

import java.util.List;

public class MemoryShower<T extends StateInterface> {

     void showValueMemory(GridPanel panelNoUsableAce, GridPanel panelUsableAce, MemoryInterface<T> memory) {
        setPanelBestValue(panelNoUsableAce, memory, false);
        setPanelBestValue(panelUsableAce, memory, true);
        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

     void setPanelBestValue(GridPanel panel, MemoryInterface<T> memory, boolean usableAce) {
        List<Integer> xSet = StateInterface.getDealerCardList();
        List<Integer> ySet = StateInterface.getHandsSumList();
        for (int y : ySet) {
            for (int x : xSet) {
                StateObserved s=new StateObserved(y, usableAce, x);
                double value = memory.readBestValue(s);
                panel.setNumbersAtCell(x,y, value);
            }
        }
        panel.setColorsAtCells();
        panel.setTextCellValues(true);
    }

    void showPolicy(GridPanel panelNoUsableAce, GridPanel panelUsableAce, StateActionValueMemory memory) {
        setPanelPolicy(panelNoUsableAce, memory, false);
        setPanelPolicy(panelUsableAce, memory, true);
        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

    void setPanelPolicy(GridPanel panel, StateActionValueMemory memory, boolean usableAce) {
        List<Integer> xSet = StateInterface.getDealerCardList();
        List<Integer> ySet = StateInterface.getHandsSumList();
        for (int y : ySet) {
            for (int x : xSet) {
                StateObserved s=new StateObserved(y, usableAce, x);
                StateObservedAction saStick= StateObservedAction.newFromStateAndAction(s, CardAction.stick);
                double valueStick = memory.read(saStick);
                StateObservedAction saHit= StateObservedAction.newFromStateAndAction(s, CardAction.hit);
                double valueHit = memory.read(saHit);
                double value=(valueHit>valueStick) ? 1 :0;
                panel.setNumbersAtCell(x,y, value);
            }
        }
        panel.setColorsAtCells();
        panel.setTextCellValues(true);
    }


}
