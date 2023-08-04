package multi_step_td.charge;

import common.ListUtils;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.PositionMapper;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPositionMapper {

    PositionMapper mapper;
    ChargeEnvironmentSettings environmentSettings;

    @BeforeEach
    public void init() {
        environmentSettings = ChargeEnvironmentSettings.newDefault();
        mapper=new PositionMapper(AgentChargeNeuralSettings.newDefault(), environmentSettings);
    }

    @Test
    public void whenPosInSite_thenPresent() {
        Assertions.assertTrue(mapper.map(0).isPresent());
    }

    @Test
    public void whenPosNoInSite_thenEmpty() {
        Assertions.assertTrue(mapper.map(29).isEmpty());
    }

    @Test
    public void whenBetween0And20_thenEqual() {
        for (int pos = 0; pos <=20 ; pos++) {
            assertEquals(pos,mapper.map(pos).orElseThrow());
        }
    }

    @Test
    public void whenSitePos_thenUnique() {
        Map<Integer,Integer> posMap=new HashMap<>();
        for (Integer posInSite:environmentSettings.siteNodes()) {
            posMap.put(posInSite,mapper.map(posInSite).orElseThrow());
        }
        System.out.println("posMap = " + posMap);
        List<Integer> list = new ArrayList<>(posMap.values());
        assertEquals(list.stream().distinct().count(),list.size());
    }


}
