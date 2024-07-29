package safe_rl.domain.trainer.recorders;

import common.other.Conditionals;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.nd4j.shade.protobuf.common.base.Preconditions.checkArgument;

/**
 *
 tracker.addMeasures(ei,s,listForPlotting );
 */

public class RecorderStateValues  {

    RecorderActionProbabilities recorder;

    public RecorderStateValues() {
        this.recorder = new RecorderActionProbabilities();
    }

    public void clear() {
        recorder.clear();
    }

    public void addStateValuesMap(@NonNull Map<Integer, List<Double>> valueMap) {
        Conditionals.executeIfTrue(!recorder.isEmpty(), () -> recorder.checkNewProbMap(valueMap));
        recorder.addStateProbabilitiesMap(valueMap);
    }

    public boolean isEmpty() {
        return recorder.isEmpty();
    }


    public List<List<Double>> valuesTrajectoryForEachAction(int state) {  //List<...>.size=nof actions
        checkArgument(recorder.recordedStates().contains(state), "State = " + state + " not recorded");
        return IntStream.range(0, recorder.nActions())
                .mapToObj(a -> recorder.probabilityTrajectoryForStateAndAction(state, a))
                .collect(Collectors.toCollection(ArrayList::new));
    }



}
