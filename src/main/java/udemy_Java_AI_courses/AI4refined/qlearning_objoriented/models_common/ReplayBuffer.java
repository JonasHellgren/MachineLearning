package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReplayBuffer {
    public final List<Experience> buffer = new ArrayList<>();

    public void addExperience(Experience experience, int REPLAY_BUFFER_MAXSIZE) {
        if (buffer.size() >= REPLAY_BUFFER_MAXSIZE)   //remove first/oldest item in set if set is "full"
            buffer.remove(0);
        buffer.add(experience);
    }

    public List<Experience> getMiniBatch(int batchLength) {
        List<Experience> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, buffer.size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        //System.out.println("buffer.size:"+buffer.size());
        //System.out.println("indexes:"+indexes);

        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));

        return miniBatch;
    }

    @Override
    public String toString() {
        return bufferAsString(buffer);
    }

    public String bufferAsString(List<Experience> buffer) {
        return "ReplayBuffer{" + System.lineSeparator() +
                buffer +
                '}';
    }
}