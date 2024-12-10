package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.gradient.NormalDistributionGradientCalculator;
import org.hellgren.utilities.normal_distribution.NormDistributionSampler;

@AllArgsConstructor
@Getter
public class AgentLunar implements AgentI{

    AgentParameters agentParameters;
    ActorMemoryLunar actorMemory;
    CriticMemoryLunar criticMemory;
    NormDistributionSampler sampler;
    NormalDistributionGradientCalculator gradCalc;

    public static AgentLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var actMemory = ActorMemoryLunar.create(p, ep);
        var critMemory = CriticMemoryLunar.zeroWeights(p, ep);
        var sampler = new NormDistributionSampler();
        var gc=new NormalDistributionGradientCalculator();
        return new AgentLunar(p,actMemory, critMemory,sampler,gc);
    }

    @Override
    public double chooseAction(StateLunar state) {
        var meanAndStd= readActor(state);
        return sampler.sampleFromNormDistribution(meanAndStd.mean(),meanAndStd.std());
    }

    @Override
    public double chooseActionNoExploration(StateLunar state) {
        var meanAndStd= readActor(state);
        return sampler.sampleFromNormDistribution(meanAndStd.mean(),0);
    }

    @Override
    public MeanAndStd fitActor(StateLunar state, double action, double adv) {
        var grad = gradientMeanAndLogStd(state, action);
        actorMemory.fit(state, adv, grad);
        return grad;
    }

    @Override
    public void fitCritic(StateLunar state, double error) {
        criticMemory.fit(state, error);
    }

    @Override
    public double readCritic(StateLunar state) {
        return criticMemory.read(state);
    }

    @Override
    public MeanAndStd readActor(StateLunar state) {
        return actorMemory.actorMeanAndStd(state);
    }

    @Override
    public MeanAndStd gradientMeanAndLogStd(StateLunar state, double action) {
        var meanAndLogStd=actorMemory.actorMeanAndLogStd(state);
        var meanAndStd= readActor(state);
        double mean = meanAndStd.mean();
        double gradMean = gradCalc.gradientMean(action, mean, meanAndStd.std());
        double gradLogStd = gradCalc.gradientLogStd(action, mean, meanAndLogStd.std());
        return MeanAndStd.of(gradMean, gradLogStd);
    }
}
