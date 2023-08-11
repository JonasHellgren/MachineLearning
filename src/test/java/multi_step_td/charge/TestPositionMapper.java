package multi_step_td.charge;

import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.PositionMapper;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        Map<Integer, Integer> posMap = createPosIndecxMap(mapper);

        posMap.entrySet().forEach(System.out::println);
        List<Integer> list = new ArrayList<>(posMap.values());
        assertEquals(list.stream().distinct().count(),list.size());
    }

    @Test
    public void whenTwoSitePosMapsCreated_thenEqual() {
        Map<Integer, Integer> posMap1 = createPosIndecxMap(mapper);
        Map<Integer, Integer> posMap2 = createPosIndecxMap(mapper);
        assertEqualMaps(posMap1, posMap2);
    }

    @Test
    public void whenTwoSitePosMapsCreatedWithDifferentMappers_thenEqual() {
        PositionMapper mapper1=new PositionMapper(AgentChargeNeuralSettings.newDefault(), environmentSettings);
        PositionMapper mapper2=new PositionMapper(AgentChargeNeuralSettings.newDefault(), environmentSettings);
        Map<Integer, Integer> posMap1 = createPosIndecxMap(mapper1);
        Map<Integer, Integer> posMap2 = createPosIndecxMap(mapper2);
        assertEqualMaps(posMap1, posMap2);
    }

    @Test
    public void whenSitePositionsList_thenIncreasing() {
        List<Integer> sitePositions=mapper.getSitePositions();

        List<Integer> diff=calcDiff(sitePositions);
        System.out.println("diff = " + diff);

        boolean anyNonPos=diff.stream().anyMatch(n -> n<0);
        System.out.println("anyNonPos = " + anyNonPos);

        assertFalse(anyNonPos);


    }

    private void assertEqualMaps(Map<Integer, Integer> posMap1, Map<Integer, Integer> posMap2) {
        for (Integer posInSite:environmentSettings.siteNodes()) {
            assertEquals(posMap1.get(posInSite), posMap2.get(posInSite));
        }
    }

    @NotNull
    private Map<Integer, Integer> createPosIndecxMap(PositionMapper mapper) {
        Map<Integer,Integer> posMap=new HashMap<>();
        for (Integer posInSite:environmentSettings.siteNodes()) {
            posMap.put(posInSite,mapper.map(posInSite).orElseThrow());
        }
        return posMap;
    }

    public static List<Integer> calcDiff(List<Integer> list) {
        if (list == null || list.size() < 2) {
            throw new IllegalArgumentException("List must contain at least two elements.");
        }

        return IntStream.range(1, list.size())
                .mapToObj(i -> list.get(i) - list.get(i - 1))
                .collect(Collectors.toList());
    }

}
