package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.radial_basis.Kernel;
import book_rl_explained.radial_basis.Kernels;
import book_rl_explained.radial_basis.RbfNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RbfMemoryFactory {

    public static RbfNetwork createMemoryManyCenters(AgentParameters p, LunarProperties ep, double learningRate) {
        var kernels= Kernels.empty();
        for (double yNorm : ep.ySpace(p.nKernelsY())) {
            for (double spdNorm : ep.spdSpace(p.nKernelsSpeed())) {
                var kernel= Kernel.ofGammas(new double[]{yNorm, spdNorm}, p.gammas());
                kernels.addKernel(kernel);
            }
        }
        return RbfNetwork.of(kernels,learningRate);
    }

    public static RbfNetwork createMemoryOneWideCenter(AgentParameters p, LunarProperties ep, double learningRate) {
        var kernels= Kernels.empty();
        kernels.addKernel(createBigKernel(p, ep));
        var mem = RbfNetwork.of(kernels,learningRate);
        mem.setWeights(new double[]{p.initWeightLogStd()});
        return mem;
    }

    private static Kernel createBigKernel(AgentParameters p, LunarProperties ep) {
        double yCenter = (ep.yMax() - ep.ySurface()) / 2;
        double spdCenter = 0d;
        return  Kernel.ofGammas(new double[]{yCenter, spdCenter}, p.gammasOneCenter());
    }

}
