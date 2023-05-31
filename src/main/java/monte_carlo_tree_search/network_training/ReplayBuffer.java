package monte_carlo_tree_search.network_training;

import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Data storage of a set of experiences, typically used for neural network training
 */

@Getter
public class ReplayBuffer<S, A>  {

    private final List<Experience<S, A>> buffer = new ArrayList<>();
    int maxSize;

    public ReplayBuffer(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addExperience(Experience<S, A> experience) {
        if (buffer.size() >= maxSize) {
            int randomIndexExcludingEnd=RandomUtils.nextInt(0,buffer.size());
            buffer.remove(randomIndexExcludingEnd);
        }
        buffer.add(experience);
    }

    public int size() {
        return buffer.size();
    }

    public void clear() {
        buffer.clear();
    }

    public Experience<S, A> getExperience(int index) {
        return buffer.get(index);
    }

    public List<Experience<S, A>> getMiniBatch(int batchLength) {
        List<Experience<S, A>> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, buffer.size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));

        return miniBatch;
    }

    public boolean isExperienceWithStateVariablesPresentBeforeIndex(S stateVariables, int iLimit) {
        for (int i = 0; i < iLimit ; i++) {
            if (buffer.get(i).stateVariables.equals(stateVariables)) {
                return  true;
            }
        }
        return  false;
    }

    public void setAllValues(double value) {
        for (Experience<S, A> exp:buffer) {
            exp.value=value;
        }
    }

    public void addAll(ReplayBuffer<S, A> otherBuffer) {
        for (Experience<S, A> experience: otherBuffer.buffer) {
            addExperience(experience);
        }
    }

    public  String bufferAsString(List<Experience<S, A>> buffer) {
        StringBuilder sb=new StringBuilder();
        sb.append(System.lineSeparator());
        for (Experience<S, A> e:buffer) {
            sb.append(e.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String toString () {
        return bufferAsString(buffer);
    }

}
