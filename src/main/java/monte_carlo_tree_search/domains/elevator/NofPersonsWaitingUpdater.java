package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import org.apache.arrow.flatbuf.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NofPersonsWaitingUpdater {

    List<Integer> stepsBetweenIncrease;
    List<PersonCounter> counters;

    public NofPersonsWaitingUpdater(List<Integer> stepsBetweenIncrease) {
        this.stepsBetweenIncrease = stepsBetweenIncrease;
        counters=new ArrayList<>();
        for(Integer steps:stepsBetweenIncrease) {
            counters.add(PersonCounter.newCounter(steps));
        }
    }

    public StateInterface<VariablesElevator> update(StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaitingList=state.getVariables().nPersonsWaiting;

        if (nPersonsWaitingList.size()!=counters.size()) {
            throw new IllegalArgumentException("Faulty length of nPersonsWaitingList");
        }

        for (PersonCounter counter:counters) {
            counter.update();
        }

        for (Integer nPersonsWaiting:nPersonsWaitingList) {
            int index=nPersonsWaitingList.indexOf(nPersonsWaiting);
            if (counters.get(index).isNofStepsBetweenReached()) {
                Integer value=nPersonsWaiting;
                value++;
                nPersonsWaitingList.set(index,value);
            }
        }

        return state;
    }

    public String toString() {
        List<Integer> integerList=new ArrayList<>();
        for (PersonCounter counter:counters) {
            integerList.add(counter.getCount());
        }

        return integerList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "{", "}"));
    }

}
