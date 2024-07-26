package domain_design_tabular_q_learning.tunnels;

import domain_design_tabular_q_learning.environments.shared.XyPos;
import domain_design_tabular_q_learning.environments.tunnels.PropertiesTunnels;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class TestPropertiesTunnels {

    PropertiesTunnels properties;

    @BeforeEach
    void init() {
        properties=PropertiesTunnels.newDefault();
    }

    @Test
    void whenAllPositions_thenCorrect() {
        var allPos=properties.allPositions();
        Assertions.assertEquals(4*9,allPos.size());
    }

    @Test
    void whenBlockedPositions_thenCorrect() {
        var bp=properties.blockedPositions();
        Assertions.assertTrue(bp.containsAll(
                List.of(XyPos.of(0,0),XyPos.of(4,0),XyPos.of(0,2),XyPos.of(8,3))));
    }


    @Test
    void whenFreePositions_thenCorrect() {
        var bp=properties.freePositions();
        Assertions.assertTrue(bp.containsAll(
                List.of(XyPos.of(1,0),XyPos.of(2,1),XyPos.of(2,2),XyPos.of(5,3))));
    }

    @Test
    void whenFailPositions_thenCorrect() {
        var fpm=properties.failPositionsRewardMap();
        var pos=fpm.keySet();
        Assertions.assertTrue(pos.containsAll(
                List.of(XyPos.of(1,0),XyPos.of(1,2),XyPos.of(5,3))));
        Assertions.assertTrue(properties.isTermFail(XyPos.of(1,0)));
        Assertions.assertEquals(-10d,properties.rewardOfFail(XyPos.of(1,0)).orElseThrow());
    }


    @Test
    void whenTerminalPositions_thenCorrect() {
        Assertions.assertTrue(properties.isTerminalNonFail(XyPos.of(3,0)));
        Assertions.assertEquals(9d,properties.rewardOfTerminalNonFail(XyPos.of(3,0)).orElseThrow());
        Assertions.assertEquals(10d,properties.rewardOfTerminalNonFail(XyPos.of(8,1)).orElseThrow());

    }

}
