package safe_rl.domain.memories;

import lombok.Builder;
import safe_rl.domain.value_classes.Experience;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static common.other.Conditionals.executeIfTrue;

@Builder
public class ReplayBuffer<V> {

    public static final int BUFFER_SIZE = 1000;
    @Builder.Default
    int maxSize= BUFFER_SIZE;
    @Builder.Default
    public final List<Experience<V>> buffer = new ArrayList<>();
    @Builder.Default
    RemoveStrategyI<V> removeStrategy=new RemoveStrategyRandom<>();

    public static <V> ReplayBuffer<V> newDefault() {  
        return ReplayBuffer.<V>builder().build();
    }

    public static <V> ReplayBuffer<V> newFromMaxSize(int maxSize) {
        return ReplayBuffer.<V>builder().maxSize(maxSize).build();
    }

    public void addExperience(Experience<V> experience) {
        executeIfTrue(size()>=maxSize, () -> removeStrategy.remove(buffer));
        buffer.add(experience);
    }

    public List<Experience<V>> getMiniBatch(int batchLength) {
        List<Experience<V>> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));
        return miniBatch;
    }

    public int size() {
        return buffer.size();
    }

    public boolean isFull() {
        return size()==maxSize;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (Experience<V> exp:buffer) {
            sb.append(exp.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();


    }
    
}
