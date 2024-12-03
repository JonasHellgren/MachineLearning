package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import common.list_arrays.ListCreator;
import lombok.AllArgsConstructor;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;

import java.util.List;

@AllArgsConstructor
public class CriticMemoryLunar {

    RadialBasis memory;
    AgentParameters parameters;

    public CriticMemoryLunar of(AgentParameters p) {

        var mem = RadialBasis.empty();
        List<Double> yListNorm= ListCreator.createFromStartToEndWithNofItems(0,1,p.nKernelsY());
        List<Double> spdListNorm= ListCreator.createFromStartToEndWithNofItems(0,1,p.nKernelsSpeed());

        for (double yNorm:yListNorm) {
            for (double spdNorm:spdListNorm) {
                mem.addKernel(KernelProperties.of(new double[]{yNorm, spdNorm}, p.gammas()));
            }
        }
        return new CriticMemoryLunar(mem, p);
    }


    double read(StateLunar state) {
        return 0;
    }

    void fit(StateLunar state, double error) {

    }


}
