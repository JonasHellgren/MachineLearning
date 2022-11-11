package black_jack.main_runner;

import black_jack.models_cards.StateObservedInterface;
import black_jack.models_cards.StateObservedObserved;
import black_jack.models_memory.MemoryInterface;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class AverageValueCalculator<T extends StateObservedInterface> {

    private static final int NOF_DECIMALS_FRAME_TITLE = 2;

    public String getAverageValue(MemoryInterface<T> stateValueMemory, boolean usableAce) {
        Predicate<StateObservedObserved> p = (usableAce)
                ?s -> s.playerHasUsableAce()
                :s -> !s.playerHasUsableAce();
        Set<Double> valueList= stateValueMemory.valuesOf(p);
        double avg= valueList.stream().filter(Objects::nonNull).mapToDouble(v -> v).average().orElse(Double.NaN);
        BigDecimal bd = BigDecimal.valueOf(avg).setScale(NOF_DECIMALS_FRAME_TITLE, RoundingMode.HALF_DOWN);
        return bd.toString();
    }

}
