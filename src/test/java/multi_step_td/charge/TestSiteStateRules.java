package multi_step_td.charge;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

public class TestSiteStateRules {

    SiteStateRules rules;


    @BeforeEach
    public void init() {
        rules = new SiteStateRules(ChargeEnvironmentSettings.newDefault());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,0.9,0.9,9,isAllFine","0,10,0.22,0.9,9,isAllFine","34,55,0.9,0.9,99,isAllFine",
            "0,1,0.19,0.9,9,isAnySoCBad","0,1,0.9,0.19,9,isAnySoCBad","0,1,0.09,0.09,9,isAnySoCBad",
            "0,0,0.9,0.9,9,isTwoAtSamePos","10,10,0.9,0.9,9,isTwoAtSamePos","24,24,0.9,0.9,9,isTwoAtSamePos",
            "30,40,0.9,0.9,9,isTwoCharging","40,50,0.9,0.9,9,isTwoCharging","32,51,0.9,0.9,9,isTwoCharging",
            "0,1,0.9,0.9,9999,isTimeUp"})

    public void whenPosAndObstacle_thenCorrectNewPos(ArgumentsAccessor arguments) {
        int posA = arguments.getInteger(0);
        int posB = arguments.getInteger(1);
        double socA = arguments.getDouble(2);
        double socB = arguments.getDouble(3);
        int time = arguments.getInteger(4);
        String siteStateName=arguments.getString(5);

        StateInterface<ChargeVariables> state= new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(socA).socB(socB)
                .time(time)
                .build());
        SiteState siteState=rules.getSiteState(state);

        Assertions.assertEquals(siteStateName, siteState.toString());
    }


}
