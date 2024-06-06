package multi_agent_rl.factories;

import com.beust.jcommander.internal.Lists;
import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.environments.apple.AppleSettings;
import multi_agent_rl.environments.apple.StateApple;
import multi_agent_rl.environments.apple.VariablesObservationApple;
import multi_agent_rl.environments.apple.VariablesStateApple;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static common.other.RandUtils.getRandomIntNumber;

@AllArgsConstructor
public class StartStateSupplierFactoryApple {

    AppleSettings settings;

    public Supplier<StateI<VariablesStateApple, VariablesObservationApple>> create(Discrete2DPos posApple) {
        return () -> {
            List<Discrete2DPos> posToAvoidList = Lists.newArrayList();
            posToAvoidList.add(posApple);
            Discrete2DPos posA = getPosA(posToAvoidList);
            posToAvoidList.add(posA);
            Discrete2DPos posB = getPosA(posToAvoidList);
            return StateApple.of(posApple, posA, posB, settings);
        };

    }

    @NotNull
    private Discrete2DPos getPosA(List<Discrete2DPos> posToAvoidList) {
        boolean isInAvoidList = true;
        Discrete2DPos posA=null;
        while (isInAvoidList) {
            posA = Discrete2DPos.of(getRandomIntNumber(0, settings.maxPos().x()), getRandomIntNumber(0, settings.maxPos().y()));
            Discrete2DPos finalPosA = posA;
            isInAvoidList = posToAvoidList.stream().anyMatch(pos -> pos.equals(finalPosA));
        }
        return posA;
    }


}
