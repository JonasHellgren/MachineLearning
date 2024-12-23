package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.RbfMemoryFactory;
import book_rl_explained.radial_basis.RbfNetwork;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@AllArgsConstructor
@Getter
public class CriticMemoryLunar {

    RbfNetwork memory;
    AgentParameters agentParameters;

    public static CriticMemoryLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var mem = RbfMemoryFactory.createMemoryManyCenters(p,ep, p.learningRateCritic());
        return new CriticMemoryLunar(mem,p);
    }

    public double read(StateLunar state) {
        return memory.outPut(List.of(state.y(),state.spd()));
    }

    public  void fit(StateLunar state, double error) {
        var data= TrainData.emptyFromErrors();
        data.addInAndError(state.asList(), error);
        memory.fit(data);
    }

/*
    public  void fit0(StateLunar state, double error) {
        var inputs = List.of(state.asList());
        var errorList = List.of(error);
        memory.fitFromErrors(inputs, errorList);
    }
*/

    public void fitBatch(List<StateLunar> states, List<Double> errors) {
        var data= TrainData.emptyFromErrors();
        for (int i = 0; i < states.size() ; i++) {
            var stateAsList=states.get(i).asList();
            data.addInAndError(stateAsList, errors.get(i));
        }
        int batchSize = Math.min(states.size(), agentParameters.batchSize());
        for (int i = 0; i < agentParameters.nEpochs() ; i++) {
            memory.fit(data.createBatch(batchSize));
        }
    }
/*
    public void fitBatch0(List<StateLunar> states, List<Double> errors) {
        var inputs = states.stream().map(StateLunar::asList).toList();
        memory.fitFromErrors(inputs, errors);
    }*/
}
