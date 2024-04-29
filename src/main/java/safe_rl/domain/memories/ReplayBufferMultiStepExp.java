package safe_rl.domain.memories;

import lombok.Builder;
import safe_rl.domain.value_classes.ExperienceMultiStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.other.Conditionals.executeIfTrue;

@Builder
public class ReplayBufferMultiStepExp<V> {

    public static final int BUFFER_SIZE = 1000;
    @Builder.Default
    int maxSize = BUFFER_SIZE;
    @Builder.Default
    public final List<ExperienceMultiStep<V>> buffer = new ArrayList<>();
    @Builder.Default
    RemoveStrategyMultiStepExpI<V> removeStrategy = new RemoveStrategyRandomMultiStepExp<>();

    public static <V> ReplayBufferMultiStepExp<V> newDefault() {
        return ReplayBufferMultiStepExp.<V>builder().build();
    }

    public static <V> ReplayBufferMultiStepExp<V> newFromMaxSize(int maxSize) {
        return ReplayBufferMultiStepExp.<V>builder().maxSize(maxSize).build();
    }

    public void addAll(List<ExperienceMultiStep<V>> experiences) {
        experiences.forEach(e -> add(e));
    }

    public void add(ExperienceMultiStep<V> experience) {
        executeIfTrue(size() >= maxSize, () -> removeStrategy.remove(buffer));
        buffer.add(experience);
    }

    public List<ExperienceMultiStep<V>> getMiniBatch(int batchLength) {
        List<ExperienceMultiStep<V>> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int i = 0; i < Math.min(batchLength, indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));
        return miniBatch;
    }


    public int size() {
        return buffer.size();
    }

    public boolean isFull() {
        return size() == maxSize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ExperienceMultiStep<V> exp : buffer) {
            sb.append(exp.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();


    }

}
