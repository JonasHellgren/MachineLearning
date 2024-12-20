package black_jack.models_memory;

import black_jack.models_cards.StateObservedAction;
import black_jack.models_cards.StateObserved;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class NumberOfStateActionsVisitsMemory  implements MemoryInterface<StateObservedAction> {

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
    public double read(StateObservedAction state) {
        return stateActionNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
    }

    @Override
    public double readBestValue(StateObserved state) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public void write(StateObservedAction state, double Value) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public double average() {
        throw new RuntimeException("Not implemented method");

    }

    @Override
    public Set<Double> valuesOf(Predicate<StateObserved> p) {
        throw new RuntimeException("Not implemented");
    }

    public void increase(StateObservedAction state) {
        int oldNofVisits=stateActionNofVisitsMap.getOrDefault(state.hashCode(),INIT_VALUE);
        stateActionNofVisitsMap.put(state.hashCode(),oldNofVisits+1);
    }

}
