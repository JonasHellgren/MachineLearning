package multi_step_temp_diff.domain.agent_parts;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.helpers_common.IntervalFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Log
@Builder
@Getter
public class ReplayBufferNStepPrioritized <S> implements ReplayBufferInterface<S> {
    private static final int BUFFER_SIZE = 10000;
    public static final int NOF_STEPS_BETWEEN_SETTING_PROBABILITIES = 10;
    public static final double TOLERANCE_PROB_ACCUM = 1e-5;
    public static final double ALPHA = 0.5d;

    @Builder.Default
    int maxSize= BUFFER_SIZE;
    @Builder.Default
    double alpha=ALPHA;
    @Builder.Default
    int nofExperienceAddingBetweenProbabilitySetting = NOF_STEPS_BETWEEN_SETTING_PROBABILITIES;
    @Builder.Default
    PrioritizationStrategyInterface<S> prioritizationStrategy=new PrioritizationProportional<>();

    final List<NstepExperience<S>> buffer = new ArrayList<>();
    final Counter addExperienceCounter=new Counter();
    final IntervalFinder intervalFinder=IntervalFinder.newNoArgumentCheck(new ArrayList<>());

    public static <S> ReplayBufferNStepUniform<S> newDefault() {  //todo remove?
        return ReplayBufferNStepUniform.<S>builder().build();
    }

    public static <S> ReplayBufferNStepUniform<S> newFromMaxSize(int maxSize) {
        return ReplayBufferNStepUniform.<S>builder().maxSize(maxSize).build();
    }

    @Override
    public void addExperience(NstepExperience<S> experience) {
        removeRandomItemIfFull();
        buffer.add(experience);
        Conditionals.executeIfTrue(isTimeToUpdate(), () ->  updateSelectionProbabilities());
        addExperienceCounter.increase();
    }

    private boolean isTimeToUpdate() {
        return addExperienceCounter.getCount() % nofExperienceAddingBetweenProbabilitySetting == 0;
    }

    public void updateSelectionProbabilities() {
        ExperiencePrioritizationSetter<S> prioritizationSetter =
                new ExperiencePrioritizationSetter<>(buffer, prioritizationStrategy);
        ExperienceProbabilitySetter<S> probabilitySetter = new ExperienceProbabilitySetter<>(buffer, alpha);
        //defineWeight(buffer);  //todo requires learn in ValueMemoryNetworkAbstract to be modified

        prioritizationSetter.setPrios();
        probabilitySetter.setProbabilities();
    }

    @Override
    public List<NstepExperience<S>> getMiniBatch(int batchLength) {
         List<Double> probabilities=new ArrayList<>();
         buffer.forEach(e -> probabilities.add(e.probability));

        List<Double> accumulatedProbabilities = getAccumulatedProbabilities(probabilities);
        intervalFinder.setInput(accumulatedProbabilities);

        List<Integer> indexes= IntStream.range(0,batchLength).boxed().map(i ->
             intervalFinder.find(RandUtils.getRandomDouble(0,1))).toList();

        List<NstepExperience<S>> miniBatch = new ArrayList<>();
        indexes.forEach(i -> miniBatch.add(buffer.get(i)));

        return miniBatch;
    }

    @NotNull
    private static List<Double> getAccumulatedProbabilities(List<Double> probabilities) {
        RunningSum<Double> runningSum=new RunningSum<>(probabilities);
        List<Double> accumulatedProbabilities=runningSum.calculate();
        if (!MathUtils.compareDoubleScalars(ListUtils.findMax(accumulatedProbabilities).orElseThrow(),1d, TOLERANCE_PROB_ACCUM)) {
            log.warning("End element in accumulated experiences differs from one, it is = "
                    +ListUtils.findMax(accumulatedProbabilities).orElseThrow());
        }
        accumulatedProbabilities=ListUtils.merge(List.of(0d),accumulatedProbabilities);
        return accumulatedProbabilities;
    }

    @Override
    public int size() {
        return buffer.size();
    }

    private void removeRandomItemIfFull() {
        if (size() >= maxSize) {
            int indexToRemove= RandUtils.getRandomIntNumber(0,size());
            buffer.remove(indexToRemove);
        }
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
