package policygradient.abstract_classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;

import java.util.Optional;

public class TestAction {

    @Test
    public void whenActionBothInAndDouble_thenThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    Action action = new Action(Optional.of(1), Optional.of(1d));
                });
    }

    @Test
    public void whenIntAction_thenCorrect() {
        Action action = Action.ofInteger(1);
        Assertions.assertEquals(1, action.asInt());
    }

    @Test
    public void whenDoubleAction_thenCorrect() {
        Action action = Action.ofDouble(1d);
        Assertions.assertEquals(1d, action.asDouble());
    }

    @Test
    public void whenIntAction_thenThrowsIfAsDouble() {
        Action action = Action.ofInteger(1);
        Assertions.assertThrows(RuntimeException.class,
                () -> {
                    var val = action.asDouble();
                });
    }

    @Test
    public void whenDoubleAction_thenThrowsIfAsInt() {
        Action action = Action.ofDouble(1d);
        Assertions.assertThrows(RuntimeException.class,
                () -> {
                    var val = action.asInt();
                });
    }

}
