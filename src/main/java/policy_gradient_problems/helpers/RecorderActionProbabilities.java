package policy_gradient_problems.helpers;

import common.Conditionals;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.nd4j.shade.protobuf.common.base.Preconditions.checkArgument;

public class RecorderActionProbabilities {

    public static final String ERROR_MESSAGE = "Non compatible new probability map";
    List<Map<Integer, List<Double>>> episodeProbabilitiesList;   // every list element is a map <state,actionProbs >

    public RecorderActionProbabilities() {
        this.episodeProbabilitiesList = new ArrayList<>();
    }

    public void clear() {
        episodeProbabilitiesList.clear();
    }

    public void addStateProbabilitiesMap(@NonNull Map<Integer, List<Double>> probMap) {
        checkNewProbMaP(probMap);
        episodeProbabilitiesList.add(probMap);
    }

    /**
     *  Returns a list with length nof episodes where each item includes probabilities of actions
     */

    public List<List<Double>> probabilitiesForState(int state) {  //List<...>.size=nof episodes
        checkArgument(recordedStates().contains(state), "State = " + state + " not recorded");
        return episodeProbabilitiesList.stream().map(map -> map.get(state)).toList();
    }

    /**
     *  Returns a list with length nof actions where each item includes nof actions trajectories
     *  Each trajectory with length equal to nof episodes
     */

    public List<List<Double>> probTrajectoryForEachAction(int state) {  //List<...>.size=nof actions
        checkArgument(recordedStates().contains(state), "State = " + state + " not recorded");
        return IntStream.range(0, nActions())
                .mapToObj(a -> probabilityTrajectoryForStateAndAction(state,a))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public List<Double> probabilityTrajectoryForStateAndAction(int state, int action) {
        return episodeProbabilitiesList.stream()
                .map(map -> map.get(state).get(action)).toList();
    }

    public Set<Integer> recordedStates() {
        return isEmpty() ? new HashSet<>() : firstRecording().keySet();
    }

    public int nStates() {
        return firstRecording().keySet().size();
    }

    public int nActions() {
        return isEmpty() ? 0 : firstRecording().get(getAnyState(firstRecording())).size();
    }

    public boolean isEmpty() {
        return episodeProbabilitiesList.isEmpty();
    }

    private Map<Integer, List<Double>> firstRecording() {
        return isEmpty() ? new HashMap<>() : episodeProbabilitiesList.get(0);
    }

    private Integer getAnyState(Map<Integer, List<Double>> integerListMap) {
        return integerListMap.keySet().stream().findFirst().orElseThrow();
    }

    private void checkNewProbMaP(@NotNull Map<Integer, List<Double>> probMap) {
        Conditionals.executeIfTrue(!isEmpty(), () -> {
                    var firstRecording = firstRecording();
                    checkArgument(probMap.keySet().containsAll(firstRecording.keySet()), ERROR_MESSAGE);
                    int anyState = getAnyState(probMap);
                    checkArgument(probMap.get(anyState).size() == nActions(), ERROR_MESSAGE);
                }
        );
    }


}
