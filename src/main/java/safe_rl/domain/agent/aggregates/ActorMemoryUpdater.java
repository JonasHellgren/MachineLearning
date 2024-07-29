package safe_rl.domain.agent.aggregates;

import common.math.NormalDistributionGradientCalculator;
import common.math.SafeGradientClipper;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;

public class ActorMemoryUpdater<V> {

    public static final double STD_MIN = 0.01;
  DisCoMemory<V> actorMean, actorLogStd; //references

  @Getter
  SafeGradientClipper meanGradClipper, stdGradClipper;

    public ActorMemoryUpdater(DisCoMemory<V> actorMean,DisCoMemory<V> actorLogStd, double gradMaxActor, double tarMeanInit,  double tarLogStdInit) {
        this.actorMean=actorMean;
        this.actorLogStd=actorLogStd;
        double tarStdInit = Math.exp(tarLogStdInit);
        meanGradClipper=SafeGradientClipper.builder()
                .gradMin(-gradMaxActor).gradMax(gradMaxActor)
                .valueMin(tarMeanInit -2*tarStdInit).valueMax(tarMeanInit +2*tarStdInit) //debatable use 2 std
                .build();
        stdGradClipper=SafeGradientClipper.builder()
                .gradMin(-gradMaxActor).gradMax(gradMaxActor)
                .valueMin(Math.log(STD_MIN)).valueMax(tarLogStdInit)
                .build();
    }

    public void fitActorMemory(StateI<V> state, double adv, Pair<Double, Double> grad) {
        double gradMean0= grad.getFirst() * adv;
        double gradStd0= grad.getSecond() * adv;
        actorMean.fitFromError(state, meanGradClipper.modify(gradMean0,actorMean.read(state)));
        actorLogStd.fitFromError(state, stdGradClipper.modify(gradStd0,actorLogStd.read(state)));
    }

}
