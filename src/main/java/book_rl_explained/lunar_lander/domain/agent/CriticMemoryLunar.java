package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import common.list_arrays.ListCreator;
import common.math.ScalerLinear;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;

import java.util.List;

@AllArgsConstructor
public class CriticMemoryLunar {

    record LunarScalers(
            ScalerLinear normalizerY,
            ScalerLinear normalizerSpd
    ) {

        public static LunarScalers of(LunarProperties ep) {
            return new LunarScalers(
                    new ScalerLinear(ep.ySurface(), ep.yMax(), 0, 1),
                    new ScalerLinear(-ep.spdMax(), ep.spdMax(), 0, 1)
        }
    }


    RadialBasis memory;
    AgentParameters parameters;
    LunarScalers scalers;

    public CriticMemoryLunar of(AgentParameters p, LunarProperties ep) {
        return new CriticMemoryLunar(createMemory(p), p, LunarScalers.of(ep));
    }

    double read(StateLunar state) {
        return memory.outPut(new double[]{
                scalers.normalizerY().calcOutDouble(state.y()),
                scalers.normalizerSpd().calcOutDouble(state.spd())});
    }

    void fit(StateLunar state, double error) {
        var updater = new WeightUpdater(memory,parameters.learningRate());
        var inputs = List.of(List.of(scalers.normalizerY().calcOutDouble(state.y()),
                scalers.normalizerSpd().calcOutDouble(state.spd())));
        var errorList = List.of(error);
        updater.updateWeightsFromErrors(inputs, errorList);
    }


    private RadialBasis createMemory(AgentParameters p) {
        var mem = RadialBasis.empty();
        var yListNorm = ListCreator.createFromStartToEndWithNofItems(0, 1, p.nKernelsY());
        var spdListNorm = ListCreator.createFromStartToEndWithNofItems(0, 1, p.nKernelsSpeed());
        for (double yNorm : yListNorm) {
            for (double spdNorm : spdListNorm) {
                mem.addKernel(KernelProperties.of(new double[]{yNorm, spdNorm}, p.gammas()));
            }
        }
        return mem;
    }

}
