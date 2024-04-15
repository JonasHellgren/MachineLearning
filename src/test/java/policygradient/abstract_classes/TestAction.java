package policygradient.abstract_classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;

import java.util.Optional;

class TestAction {

    @Test
    void whenActionBothInAndDouble_thenThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Action(Optional.of(1), Optional.of(1d),false));
    }

    @Test
    void whenIntAction_thenCorrect() {
        Action action = Action.ofInteger(1);
        Assertions.assertEquals(1, action.asInt());
    }

    @Test
     void whenDoubleAction_thenCorrect() {
        Action action = Action.ofDouble(1d);
        Assertions.assertEquals(1d, action.asDouble());
    }

    @Test
    void whenDoubleActionAndSafeCorrected_thenCorrect() {
        Action action = Action.ofDoubleSafeCorrected(1d);
        Assertions.assertEquals(1d, action.asDouble());
        Assertions.assertTrue(action.isSafeCorrected());

    }

    @Test
     void whenIntAction_thenThrowsIfAsDouble() {
        Action action = Action.ofInteger(1);
        Assertions.assertThrows(RuntimeException.class, () -> action.asDouble());
    }

    @Test
     void whenDoubleAction_thenThrowsIfAsInt() {
        Action action = Action.ofDouble(1d);
        Assertions.assertThrows(RuntimeException.class, () -> action.asInt());
    }

}
