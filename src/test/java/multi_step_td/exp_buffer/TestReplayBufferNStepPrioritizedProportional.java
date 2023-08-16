package multi_step_td.exp_buffer;

import common.CpuTimer;
import common.ListUtils;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.PrioritizationProportional;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/***
 * nofExperienceAddingBetweenProbabilitySetting = 10 significantly speeds up
 *
 */

public class TestReplayBufferNStepPrioritizedProportional {

    public static final double DELTA_ACCUM_PROB = 1e-10;
    ReplayBufferNStepPrioritized<ForkVariables> prioritizedBuffer;
    List<Double> TD_ERRORS= List.of(1d,2d,1d);

    @BeforeEach
    public void init() {
        prioritizedBuffer=ReplayBufferNStepPrioritized.<ForkVariables>builder()
                .alpha(1)
                .nofExperienceAddingBetweenProbabilitySetting(10)
                .prioritizationStrategy(new PrioritizationProportional<>())
                .build();

        TD_ERRORS.forEach(tdError ->
                addExperienceWithTdError(tdError));
        prioritizedBuffer.updateSelectionProbabilities();
    }

    @Test
    public void whenAddedExperiences_thenCorrectSelectionProbabilities() {
        List<Double> probabilities = getProbabilities();
        System.out.println("probabilities = " + probabilities);
        double probSum = ListUtils.sumList(TD_ERRORS);
        assertTrue(probabilities.containsAll(List.of(2d/ probSum,1d/probSum)));
        assertEquals(0.5,probabilities.get(2-1));
    }

    @Test
    public void whenGettingMiniBatch_thenCorrectAccumulatedProbabilities() {
        prioritizedBuffer.getMiniBatch(1);
        List<Double> probAccumList = getAccumProbabilites();
        System.out.println("probAccumList = " + probAccumList);
        List<Double> probabilities = getProbabilities();
        assertTrue(probAccumList.containsAll(List.of(0d,probabilities.get(0),1d)));
    }

    private List<Double> getAccumProbabilites() {
        return prioritizedBuffer.getIntervalFinder().getInput();
    }

    @Test
    public void whenGettingMiniBatchMultipleTimes_thenItemWithHighestTDErrorIsFrequent() {
        int nofBatches = 10;

        List<Integer> indexes= IntStream.range(0,nofBatches).boxed().map(i ->
        {
            var miniBatch = prioritizedBuffer.getMiniBatch(1);
            return prioritizedBuffer.getBuffer().indexOf(miniBatch.get(0));
        }).toList();

        int nofSecondItem=Collections.frequency(indexes, 1);

        System.out.println("indexes = " + indexes);
        assertTrue(nofSecondItem> TD_ERRORS.size()/nofBatches);
    }

    @Test
    public void whenTenThousandItemsInBuffer_thenCorrectAccumulatedProbabilities() {
        int nofItems = 10_000;
        createBufferWithWithSameTdErrorItems(nofItems);

        CpuTimer timer=new CpuTimer();
        prioritizedBuffer.updateSelectionProbabilities();
        System.out.println("time single update probabilities etc = " + timer.absoluteProgressInMillis());

        timer.reset();
        prioritizedBuffer.getMiniBatch(1);
        System.out.println("time getMiniBatch is = " + timer.absoluteProgressInMillis());

        List<Double> probabilities = getProbabilities();
        List<Double> probAccumList = getAccumProbabilites();

        assertTrue(probAccumList.containsAll(List.of(0d,probabilities.get(0))));
        assertEquals(ListUtils.findMax(probAccumList).orElseThrow(),1d, DELTA_ACCUM_PROB);

    }

    @Test
    public void whenTenThousandItemsInBuffer_thenDifferentItemsInMiniBatch() {
        int nofItems = 10_000;
        createBufferWithWithSameTdErrorItems(nofItems);

        List<NstepExperience<ForkVariables>> miniBatch= prioritizedBuffer.getMiniBatch(10);
        List<Integer> indexes= IntStream.range(0,miniBatch.size()).boxed().map(i ->
                prioritizedBuffer.getBuffer().indexOf(miniBatch.get(i))).toList();

        Set<Integer> indexSet=new HashSet<>(indexes);

        System.out.println("indexes = " + indexes);

        assertEquals(indexSet.size(),indexes.size());

    }



    private void createBufferWithWithSameTdErrorItems(int nofItems) {
        prioritizedBuffer.getBuffer().clear();
        List<Double> tdErrors=IntStream.range(0, nofItems).mapToDouble(i -> (double) i/ nofItems).boxed().toList();
        tdErrors.forEach(tdError ->  addExperienceWithTdError(tdError));
    }

    private void addExperienceWithTdError(Double tdError) {
        prioritizedBuffer.addExperience(NstepExperience.<ForkVariables>builder()
                .stateToUpdate(ForkState.newFromRandomPos())
                .tdError(tdError).build());
    }

        @NotNull
    private List<Double> getProbabilities() {
        return prioritizedBuffer.getBuffer().stream().map(e -> e.probability).toList();
    }


}
