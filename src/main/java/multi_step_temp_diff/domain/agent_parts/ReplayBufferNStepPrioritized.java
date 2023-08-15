package multi_step_temp_diff.domain.agent_parts;

import common.RandUtils;
import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.helpers_common.IntervalFinder;
import java.util.ArrayList;
import java.util.List;

public class ReplayBufferNStepPrioritized <S> implements ReplayBufferInterface<S> {
    private static final int BUFFER_SIZE = 10000;
    @Builder.Default
    int maxSize= BUFFER_SIZE;
    @Builder.Default
    public final List<NstepExperience<S>> buffer = new ArrayList<>();

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

        //setPrioritization(buffer,strategyPrio);
        //setProbability(buffer);
        //defineWeight(buffer);  //todo requires learn in ValueMemoryNetworkAbstract to be modified

        //sometimesDefinePrioProbAndWeightForAllExperiences();

    }

    @Override
    public List<NstepExperience<S>> getMiniBatch(int batchLength) {

        //defineProbAccumList()

        List<Double> probAccumList=new ArrayList<>();
        IntervalFinder intervalFinder=IntervalFinder.newNoArgumentCheck(probAccumList);

        List<Integer> indexes=new ArrayList<>();
        for (int i = 0; i < maxSize ; i++) {  //todo use streams
            double randomProbAccum= RandUtils.getRandomDouble(0,1);
            int index=intervalFinder.find(randomProbAccum);
            indexes.add(index);
        }


        List<NstepExperience<S>> miniBatch = new ArrayList<>();
        for (int i = 0; i < Math.min(batchLength,indexes.size()); i++)
            miniBatch.add(buffer.get(indexes.get(i)));

        return miniBatch;
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
