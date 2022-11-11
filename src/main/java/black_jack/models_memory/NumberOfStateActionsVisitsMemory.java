package black_jack.models_memory;

import black_jack.models_cards.StateObservedActionObserved;
import black_jack.models_cards.StateObservedObserved;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class NumberOfStateActionsVisitsMemory  implements MemoryInterface<StateObservedActionObserved> {

    public static final int INIT_VALUE = 0;
    Map<Integer, Integer> stateActionNofVisitsMap;

    public NumberOfStateActionsVisitsMemory() {
        this.stateActionNofVisitsMap = new HashMap<>();
    }

    @Override
    public void clear() {
        stateActionNofVisitsMap.clear();
    }

    @Override
    public int nofItems() {
        return stateActionNofVisitsMap.size();
    }

    @Override
    public double read(StateObservedActionObserved state) {
        return stateActionNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
    }

    @Override
    public double readBestValue(StateObservedObserved state) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public void write(StateObservedActionObserved state, double Value) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public double average() {
        throw new RuntimeException("Not implemented method");

    }

    @Override
    public Set<Double> valuesOf(Predicate<StateObservedObserved> p) {
        throw new RuntimeException("Not implemented");
    }

    public void increase(StateObservedActionObserved state) {
        int oldNofVisits=stateActionNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
        stateActionNofVisitsMap.put(state.hashCode(),oldNofVisits+1);
    }

}
