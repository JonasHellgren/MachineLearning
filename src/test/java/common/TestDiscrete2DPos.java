package common;

import common.math.Discrete2DPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestDiscrete2DPos {

    static Discrete2DPos pos11= Discrete2DPos.of(1,1);
    static Discrete2DPos pos21= Discrete2DPos.of(2,1);
    static Discrete2DPos pos31= Discrete2DPos.of(3,1);

    @Test
    void mid11And21Empty() {
        var mid=pos11.midPos(pos21);
        Assertions.assertTrue(mid.isEmpty());
    }

    @Test
    void givenMid11And31_thenMidIs21() {
        var mid=pos11.midPos(pos31);
        Assertions.assertTrue(mid.isPresent());
        Assertions.assertTrue(mid.get().equals(Discrete2DPos.of(2,1)));
    }


}
