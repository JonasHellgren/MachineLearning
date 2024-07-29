package safe_rl.domain.agent.aggregates;

import common.math.SafeGradientClipper;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.value_objects.TrainerParametersInterpreter;
import safe_rl.domain.environment.aggregates.StateI;

public class ActorMemoryUpdater<V> {

    public static final double STD_MIN = 0.01;
    DisCoMemory<V> actorMean, actorLogStd; //references
    @Getter
    SafeGradientClipper meanGradClipper, stdGradClipper;

    public ActorMemoryUpdater(DisCoMemory<V> actorMean,
                              DisCoMemory<V> actorLogStd,
                              TrainerParametersInterpreter parameters) {
        this.actorMean = actorMean;
        this.actorLogStd = actorLogStd;
        double tarStdInit = Math.exp(parameters.targetLogStd());
        meanGradClipper = SafeGradientClipper.builder()
                .gradMin(-parameters.gradMaxActor())
                .gradMax(parameters.gradMaxActor())
                .valueMin(parameters.targetMean() - 2 * tarStdInit)
                .valueMax(parameters.targetMean() + 2 * tarStdInit) //debatable use 2 std
                .build();
        stdGradClipper = SafeGradientClipper.builder()
                .gradMin(-parameters.gradMaxActor())
                .gradMax(parameters.gradMaxActor())
                .valueMin(Math.log(STD_MIN))
                .valueMax(parameters.targetLogStd())
                .build();
    }

    public void fitActorMemory(StateI<V> state, double adv, Pair<Double, Double> grad) {
        double gradMean0 = grad.getFirst() * adv;
        double gradStd0 = grad.getSecond() * adv;
        actorMean.fitFromError(state, meanGradClipper.modify(gradMean0, actorMean.read(state)));
        actorLogStd.fitFromError(state, stdGradClipper.modify(gradStd0, actorLogStd.read(state)));
    }

}
