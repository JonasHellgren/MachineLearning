package multi_step_td.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.PositionTransitionRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

public class TestPositionTransitionRules {

    PositionTransitionRules rules;

    @BeforeEach
    public void init() {
        rules=new PositionTransitionRules(ChargeEnvironmentSettings.newDefault());
    }

    @ParameterizedTest
    @CsvSource({
            "0,false,0,1",
            "7,false,0,7", "7,false,1,8",
            "8,false,0,9", "8,true,0,8",
            "10,false,0,11", "10,false,1,20",
            "20,false,0,20", "20,false,1,30",
            "50,false,0,51", "50,false,1,51",
            "32,false,0,22", "32,false,1,22",
            "22,false,0,22", "22,false,1,12",
            "13,false,0,14", "13,false,1,14",
            "13,true,0,14", "13,true,1,14",
            "29,true,0,29", "29,true,1,29",
    })

    public void whenPosAndObstacle_thenCorrectNewPos(ArgumentsAccessor arguments) {
        int pos = arguments.getInteger(0);
        boolean isObstacle = arguments.getBoolean(1);
        int command = arguments.getInteger(2);
        int expectedNewPos = arguments.getInteger(3);
        int newPos=rules.getNewPos(pos,isObstacle,command);
        Assertions.assertEquals(expectedNewPos,newPos);
    }

}
