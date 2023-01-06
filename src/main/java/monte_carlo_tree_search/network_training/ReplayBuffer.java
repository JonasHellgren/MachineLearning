package monte_carlo_tree_search.network_training;

import lombok.Getter;
import org.apache.commons.math3.analysis.function.Exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class ReplayBuffer<SSV, AV>  {

    private final List<Experience<SSV, AV>> buffer = new ArrayList<>();
    int maxSize;

    public ReplayBuffer(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addExperience(Experience<SSV, AV> experience) {
        if (buffer.size() >= maxSize)   //remove first/oldest item in set if set is "full"
            buffer.remove(0);   //todo, remove random instead
        buffer.add(experience);
    }

    public int size() {
        return buffer.size();
    }

    public void clear() {
        buffer.clear();
    }

    public Experience<SSV, AV> getExperience(int index) {
        return buffer.get(index);
    }

    public List<Experience<SSV, AV>> getMiniBatch(int batchLength) {
        List<Experience<SSV, AV>> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, buffer.size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));

        return miniBatch;
    }

    public boolean isExperienceWithStateVariablesPresentBeforeIndex(SSV stateVariables, int iLimit) {
        for (int i = 0; i < iLimit ; i++) {
            if (buffer.get(i).stateVariables.equals(stateVariables)) {
                return  true;
            }
        }
        return  false;
    }

    public void setAllValues(double value) {
        for (Experience<SSV,AV> exp:buffer) {
            exp.value=value;
        }
    }

    public void addAll(ReplayBuffer<SSV, AV> otherBuffer) {
        for (Experience<SSV, AV> experience: otherBuffer.buffer) {
            addExperience(experience);
        }
    }

    public  String bufferAsString(List<Experience<SSV, AV>> buffer) {
        StringBuilder sb=new StringBuilder();
        sb.append(System.lineSeparator());
        for (Experience<SSV,AV> e:buffer) {
            sb.append(e.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String toString () {
        return bufferAsString(buffer);
    }

}
