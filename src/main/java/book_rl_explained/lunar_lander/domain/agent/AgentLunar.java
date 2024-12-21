package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.gradient.NormalDistributionGradientCalculator;
import org.hellgren.utilities.normal_distribution.NormDistributionSampler;

import java.util.ArrayList;
import java.util.List;

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
        var criticMemory = CriticMemoryLunar.zeroWeights(p, ep);
        var sampler = new NormDistributionSampler();
        var gc=new NormalDistributionGradientCalculator();
        return new AgentLunar(p,actMemory, criticMemory,sampler,gc);
    }

    @Override
    public double chooseAction(StateLunar state) {
        var meanAndStd= readActor(state);
        return sampler.sampleFromNormDistribution(meanAndStd.mean(),meanAndStd.std());
    }

    @Override
    public double chooseActionNoExploration(StateLunar state) {
        var meanStd= readActor(state);
        return sampler.sampleFromNormDistribution(meanStd.mean(),0);
    }

    @Override
    public void fitActor(StateLunar state, double action, double adv) {
        var grad = gradientMeanAndLogStd(state, action);
        actorMemory.fit(state, adv, grad);
    }


    @Override
    public void fitActorBatch(List<StateLunar> states, List<Double> actions, List<Double> advantages) {
        //var grads=advantages.stream().map(adv->gradientMeanAndLogStd(states.get(0), actions.get(0)).asList()).toList();
        List<GradientMeanStd> grads=new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            grads.add(gradientMeanAndLogStd(states.get(i), actions.get(i)));
        }
        actorMemory.fitBatch(states, actions, grads);
    }

    @Override
    public void fitCritic(StateLunar state, double error) {
        criticMemory.fit(state, error);
    }

    @Override
    public void fitCriticBatch(List<StateLunar> states, List<Double> errors) {
        criticMemory.fitBatch(states, errors);
    }

    @Override
    public double readCritic(StateLunar state) {
        return criticMemory.read(state);
    }

    @Override
    public MeanStd readActor(StateLunar state) {
        return actorMemory.actorMeanAndStd(state);
    }

    @Override
    public GradientMeanStd gradientMeanAndLogStd(StateLunar state, double action) {
        var meanAndLogStd=actorMemory.actorMeanAndLogStd(state);
        var meanAndStd= readActor(state);
        double mean = meanAndStd.mean();
        double gradMean = gradCalc.gradientMean(action, mean, meanAndStd.std());
        double gradLogStd = gradCalc.gradientLogStd(action, mean, meanAndLogStd.std());
        return GradientMeanStd.of(gradMean, gradLogStd);
    }

}
