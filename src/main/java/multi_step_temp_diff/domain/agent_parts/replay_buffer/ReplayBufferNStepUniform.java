package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import common.other.CpuTimer;
import lombok.Builder;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy.RemoveBufferExperienceStrategyInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy.RemoveStrategyRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.other.Conditionals.executeIfTrue;
import static it.unimi.dsi.fastutil.io.TextIO.BUFFER_SIZE;


@Builder
public class ReplayBufferNStepUniform<S> implements ReplayBufferInterface<S> {

    @Builder.Default
    int maxSize= BUFFER_SIZE;
    @Builder.Default
    public final List<NstepExperience<S>> buffer = new ArrayList<>();
    @Builder.Default
    RemoveBufferExperienceStrategyInterface<S> removeStrategy=new RemoveStrategyRandom<>();

    public static <S> ReplayBufferNStepUniform<S> newDefault() {  //todo remove?
        return ReplayBufferNStepUniform.<S>builder().build();
    }

    public static <S> ReplayBufferNStepUniform<S> newFromMaxSize(int maxSize) {
        return ReplayBufferNStepUniform.<S>builder().maxSize(maxSize).build();
    }

    @Override
    public void addExperience(NstepExperience<S> experience, CpuTimer timer) {
        executeIfTrue(size()>=maxSize, () -> removeStrategy.remove(buffer));
        buffer.add(experience);
    }

    @Override
    public List<NstepExperience<S>> getMiniBatch(int batchLength) {
        List<NstepExperience<S>> miniBatch = new ArrayList<>();

        List<Integer> indexes = IntStream.rangeClosed(0, size() - 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));

        return miniBatch;
    }

    @Override
    public int size() {
        return buffer.size();
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
