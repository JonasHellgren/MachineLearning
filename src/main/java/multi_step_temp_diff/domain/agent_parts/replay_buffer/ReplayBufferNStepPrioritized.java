package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy.PrioritizationProportional;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy.PrioritizationStrategyInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.ExperiencePrioritizationSetter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.ExperienceProbabilitySetter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.ExperienceWeightSetter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.RunningSum;
import multi_step_temp_diff.domain.helpers_common.IntervalFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static it.unimi.dsi.fastutil.io.TextIO.BUFFER_SIZE;


/***
 * Every experience is assigned a probability, if this probability is high it is more likely to be included in a
 * minibatch.
 *
 * Example:
 * probabilities=[0.5, 0.25,0.25]  -> accumulatedProbabilities=[0,0.5,0.75,1.0]
 * A number randomBetweenZeroAndOneToPointOutExperience points out an interval in accumulatedProbabilities
 * For example do 0.3 point out interval zero and 0.9 interval 2.
 * In this example interval 0, the very first experience, is most likely to be selected.

 */

@Log
@Builder
@Getter
public class ReplayBufferNStepPrioritized <S> implements ReplayBufferInterface<S> {
    public static final int NOF_STEPS_BETWEEN_SETTING_PROBABILITIES = 10;
    public static final double TOLERANCE_PROB_ACCUM = 1e-5;
    public static final double ALPHA = 0.5d;
    public static final double BETA0 = 0.5,BETA1 = 1.0;
    public static final double EPSILON = 0.01;

    @Builder.Default
    int maxSize= BUFFER_SIZE;
    @Builder.Default
    double alpha=ALPHA;
    @Builder.Default
    double beta0= BETA0;
    @Builder.Default
    double beta1= BETA1;
    @Builder.Default
    int nofExperienceAddingBetweenProbabilitySetting = NOF_STEPS_BETWEEN_SETTING_PROBABILITIES;
    @Builder.Default
    PrioritizationStrategyInterface<S> prioritizationStrategy=new PrioritizationProportional<>(EPSILON);

    final List<NstepExperience<S>> buffer = new ArrayList<>();
    final Counter addExperienceCounter=new Counter();
    final IntervalFinder intervalFinder=IntervalFinder.newNoArgumentCheck(new ArrayList<>());

    public static <S> ReplayBufferNStepPrioritized<S> newDefault() {  //todo remove?
        return ReplayBufferNStepPrioritized.<S>builder().build();
    }

    public static <S> ReplayBufferNStepPrioritized<S> newFromMaxSize(int maxSize) {
        return ReplayBufferNStepPrioritized.<S>builder().maxSize(maxSize).build();
    }

    /***
     * updateSelectionProbabilities() is potentially costly to execute
     * On the other hand, if not executed, newly added experiences are not updated regarding
     * prioritization, probability etc. The approach is to initialize experience with a selection probability of zero.
     * Thereby the accumulated probabilities are valid.
     * The consequence is that newly added experiences can not be selected by getMiniBatch() until
     * updateSelectionProbabilities() is called again.
     */

    @Override
    public void addExperience(NstepExperience<S> experience,CpuTimer timer) {
        boolean isRemoved=removeRandomItemIfFull();
        buffer.add(experience);
        double beta=BETA0+(BETA1-BETA0)*timer.relativeProgress();
        Conditionals.executeIfTrue(isTimeToUpdate() || isRemoved,
                () ->  updatePrioritizationSelectionProbabilitiesAndWeights(beta));
        addExperienceCounter.increase();
    }

    private boolean isTimeToUpdate() {
        return addExperienceCounter.getCount() % nofExperienceAddingBetweenProbabilitySetting == 0;
    }

    public void updatePrioritizationSelectionProbabilitiesAndWeights(double beta) {
        ExperiencePrioritizationSetter<S> prioritizationSetter =
                new ExperiencePrioritizationSetter<>(buffer, prioritizationStrategy);
        ExperienceProbabilitySetter<S> probabilitySetter = new ExperienceProbabilitySetter<>(buffer, alpha);
        ExperienceWeightSetter<S> experienceWeightSetter=new ExperienceWeightSetter<>(buffer,beta);

        prioritizationSetter.setPrios();
        probabilitySetter.setProbabilities();
        experienceWeightSetter.setWeights();
    }

    @Override
    public List<NstepExperience<S>> getMiniBatch(int batchLength) {
        List<Double> probabilities=buffer.stream().map(e -> e.probability).toList();

        List<Double> accumulatedProbabilities = getAccumulatedProbabilities(probabilities);
        intervalFinder.setInput(ListUtils.merge(List.of(0d),accumulatedProbabilities));
        List<Integer> indexes= IntStream.range(0,batchLength).boxed().map(i ->
        {
            double randomBetweenZeroAndOneToPointOutExperience = RandUtils.getRandomDouble(0, 1);
            return intervalFinder.find(randomBetweenZeroAndOneToPointOutExperience);
        }).toList();

        return indexes.stream().map(i ->buffer.get(i)).toList();
    }

    @NotNull
    private static List<Double> getAccumulatedProbabilities(List<Double> probabilities) {
        RunningSum<Double> runningSum=new RunningSum<>(probabilities);
        List<Double> accumulatedProbabilities=runningSum.calculate();

        if (!MathUtils.compareDoubleScalars(ListUtils.findMax(accumulatedProbabilities).orElseThrow(),1d, TOLERANCE_PROB_ACCUM)) {
            log.warning("End element in accumulated experiences differs from one, it is = "
                    +ListUtils.findMax(accumulatedProbabilities).orElseThrow());
        }
        return accumulatedProbabilities;
    }

    @Override
    public int size() {
        return buffer.size();
    }

    private boolean removeRandomItemIfFull() {
        if (size() >= maxSize) {
            int indexToRemove= RandUtils.getRandomIntNumber(0,size());
            buffer.remove(indexToRemove);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (NstepExperience<S> exp:buffer) {
            sb.append(exp.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();


    }

}
