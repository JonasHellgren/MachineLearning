package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.apache.commons.math3.util.Pair;

public class AgentLunar implements AgentI{

    AgentParameters parameters;
    ActorMemoryLunar actorMemory;
    CriticMemoryLunar criticMemory;

    @Override
    public double chooseAction(StateLunar state) {
        return 0;
    }

    @Override
    public Pair<Double, Double> fitActor(StateLunar state, double action, double adv) {
        return null;
    }

    @Override
    public void fitCritic(StateLunar state, double error) {

    }

    @Override
    public double readCritic(StateLunar state) {
        return 0;
    }

    @Override
    public Pair<Double, Double> gradientMeanAndStd(StateLunar state, double action) {
        return null;
    }
}
