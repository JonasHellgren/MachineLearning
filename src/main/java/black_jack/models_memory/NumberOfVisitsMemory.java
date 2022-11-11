package black_jack.models_memory;

import black_jack.models_cards.StateObserved;

import java.util.HashMap;
import java.util.Map;

public class NumberOfVisitsMemory implements MemoryInterface<StateObserved> {

    public static final int INIT_VALUE = 0;

    Map<Integer, Integer> stateNofVisitsMap;

    public NumberOfVisitsMemory() {
        this.stateNofVisitsMap = new HashMap<>();
    }

    @Override
    public void clear() {
        this.stateNofVisitsMap.clear();
    }

    @Override
    public int nofItems() {
        return stateNofVisitsMap.size();
    }

    @Override
    public double read(StateObserved state) {
        return stateNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
    }

    @Override
    public void write(StateObserved state, double Value) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public double average() {
        int sum = stateNofVisitsMap.values().stream().mapToInt(d -> d).sum();
        int nofItems=stateNofVisitsMap.size();
        return (nofItems==0)
                ? 0
                :(double) sum/(double)nofItems;
    }

    public void increase(StateObserved state) {
        int oldNofVisits=stateNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
        stateNofVisitsMap.put(state.hashCode(),oldNofVisits+1);
    }
}
