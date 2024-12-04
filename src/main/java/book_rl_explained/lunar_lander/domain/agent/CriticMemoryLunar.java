package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;

import java.util.List;

import static org.hellgren.utilities.list_arrays.ListCreator.createFromStartToEndWithNofItems;

@AllArgsConstructor
public class CriticMemoryLunar {

/*    public record LunarScalers(
            ScalerLinear normalizerY,
            ScalerLinear normalizerSpd
    ) {

        public static LunarScalers of(LunarProperties ep) {
            return new LunarScalers(
                    new ScalerLinear(ep.ySurface(), ep.yMax(), 0, 1),
                    new ScalerLinear(-ep.spdMax(), ep.spdMax(), 0, 1));
        }
    }*/

    @Getter
    RadialBasis memory;
    WeightUpdater updater;
    //LunarScalers scalers;

    public static CriticMemoryLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var mem = createMemory(p,ep);
        var updater0 = new WeightUpdater(mem,p.learningRate());
      //  var lunarScalers = LunarScalers.of(ep);
        return new CriticMemoryLunar(mem, updater0);
    }

    public double read(StateLunar state) {
       // return memory.outPut(new double[]{getYNorm(state),getSpdNorm(state)});
        return memory.outPut(new double[]{state.y(),state.spd()});
    }

    public  void fit(StateLunar state, double error) {
        var inputs = List.of(List.of(state.y(),state.spd()));
        var errorList = List.of(error);
        updater.updateWeightsFromErrors(inputs, errorList);
    }
/*

    private double getSpdNorm(StateLunar state) {
        return scalers.normalizerSpd().calcOutDouble(state.spd());
    }

    private double getYNorm(StateLunar state) {
        return scalers.normalizerY().calcOutDouble(state.y());
    }
*/


    private static RadialBasis createMemory(AgentParameters p, LunarProperties ep) {
        var mem = RadialBasis.empty();
        var yList = createFromStartToEndWithNofItems(ep.ySurface(), ep.yMax(), p.nKernelsY());
        var spdList = createFromStartToEndWithNofItems(-ep.spdMax(), ep.spdMax(), p.nKernelsSpeed());
        for (double yNorm : yList) {
            for (double spdNorm : spdList) {
                mem.addKernel(KernelProperties.of(new double[]{yNorm, spdNorm}, p.gammas()));
            }
        }
        return mem;
    }

}
