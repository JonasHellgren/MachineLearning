package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.MemoryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;

import java.util.List;

@AllArgsConstructor
public class CriticMemoryLunar {

    @Getter
    RadialBasis memory;
    WeightUpdater updater;

    public static CriticMemoryLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var mem = MemoryFactory.createMemoryManyCenters(p,ep);
        var updater0 = new WeightUpdater(mem,p.learningRateCritic());
        return new CriticMemoryLunar(mem, updater0);
    }

    public double read(StateLunar state) {
        return memory.outPut(new double[]{state.y(),state.spd()});
    }

    public  void fit(StateLunar state, double error) {
        var inputs = List.of(state.asList());
        var errorList = List.of(error);
        updater.updateWeightsFromErrors(inputs, errorList);
    }

}
