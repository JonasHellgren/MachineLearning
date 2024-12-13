package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;

import java.util.List;

import static org.hellgren.utilities.list_arrays.ListCreator.createFromStartToEndWithNofItems;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryFactory {

    public static RadialBasis createMemoryManyCenters(AgentParameters p, LunarProperties ep) {
        var mem = RadialBasis.empty();
        for (double yNorm : ep.ySpace(p.nKernelsY())) {
            for (double spdNorm : ep.spdSpace(p.nKernelsSpeed())) {
                mem.addKernel(KernelProperties.of(new double[]{yNorm, spdNorm}, p.gammas()));
            }
        }
        return mem;
    }

    public static RadialBasis createMemoryOneWideCenter(AgentParameters p, LunarProperties ep) {
        var mem = RadialBasis.empty();
        addBigKernel(p, ep, mem);
        mem.setWeights(new double[]{p.initWeightLogStd()});
        return mem;
    }

    private static void addBigKernel(AgentParameters p, LunarProperties ep, RadialBasis mem) {
        double yCenter = (ep.yMax() - ep.ySurface()) / 2;
        double spdCenter = 0d;
        mem.addKernel(KernelProperties.of(new double[]{yCenter, spdCenter}, p.gammasOneCenter()));
    }

}
