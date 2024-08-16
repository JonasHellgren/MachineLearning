package policygradient.short_corridor;

import common.other.RandUtilsML;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.environments.short_corridor.NeuralCriticMemorySC;
import policy_gradient_problems.environments.short_corridor.StateSC;
import policy_gradient_problems.environments.short_corridor.VariablesSC;
import java.util.*;

 class TestNeuralCriticMemorySC {

     static final double VAL0 = -0.5, VAL1=0.5, VAL2=VAL0;
     static final double TOL = 0.2;
     static final int NOF_CASES = 3;
     public static final int POS0 = 0,POS1 = 1,POS2 = 2;  //observed pos
     static NeuralCriticMemorySC critic;
    static Map<Integer,Double> svMapPrev;


    @SneakyThrows
    @BeforeAll
     static  void init() {
        critic=NeuralCriticMemorySC.newDefault();
        List<List<Double>> valuesList=trainCritic();
        printStateValues();

        plotValuesVersusEpisode(valuesList,List.of("0","1","2"));

        Thread.sleep(15000);
    }

    @Test
     void whenObsState0_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromObsPos(POS0));
        Assertions.assertEquals(VAL0,value, TOL);
    }

    @Test
     void whenObsState1_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromObsPos(POS1));
        Assertions.assertEquals(VAL1,value, TOL);
    }


    @Test
     void whenObsState2_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromObsPos(POS2));
        Assertions.assertEquals(VAL2,value, TOL);
    }


    static List<List<Double>> trainCritic() {
        var caseSGtMap = getCaseSAGtMap();
        List<List<Double>> valuesList=new ArrayList<>();
        List<Double> values0=new ArrayList<>();
        List<Double> values1=new ArrayList<>();
        List<Double> values2=new ArrayList<>();

        for (int ei = 0; ei < 1000 ; ei++) {
            int caseNr= RandUtilsML.getRandomIntNumber(0, NOF_CASES);
            var pair=caseSGtMap.get(caseNr);
            var state= StateSC.newFromObsPos(pair.getLeft());
            List<Double> in = state.asList();
            List<Double> out = List.of(pair.getRight());

/*
            System.out.println("in = " + in);
            System.out.println("out = " + out);
*/

            critic.fit(List.of(in), out);
            List<Double> values=getStateValueMap().values().stream().toList();
            values0.add(values.get(0));
            values1.add(values.get(1));
            values2.add(values.get(2));
        }
        valuesList.add(values0);
        valuesList.add(values1);
        valuesList.add(values2);

        return valuesList;
    }

    @SneakyThrows
    private static void plotValuesVersusEpisode(List<List<Double>> listList, List<String> titles) {
        var chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle("v").width(350).height(200).build();
        var titlesIterator = titles.iterator();
        System.out.println("listList.size() = " + listList.size());
        System.out.println("titles.size() = " + titles.size());
        for (List<Double> doubles : listList) {
            var series = chart.addSeries(titlesIterator.next(), null, doubles);
            series.setMarker(SeriesMarkers.NONE);
        }
        new SwingWrapper<>(chart).displayChart();
    }

    private static void printStateValues() {
        Map<Integer, Pair<Integer, Double>> caseSAGtMap = getCaseSAGtMap();

        for (int ci = 0; ci < NOF_CASES ; ci++) {
            var pair=caseSAGtMap.get(ci);
            var estOut=critic.getOutValue(StateSC.newFromObsPos(pair.getLeft()));
            System.out.println("inData = " + pair+", estOut = " + estOut);
        }
    }

    @NotNull
    private static Map<Integer, Double> getStateValueMap() {
        Map<Integer,Double> svMap=new HashMap<>();
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; pos++) {
            StateI<VariablesSC> stateSC = StateSC.newFromObsPos(pos);
            svMap.put(pos,critic.getOutValue(stateSC));
        }
        return svMap;
    }

    private static Map<Integer, Pair<Integer, Double>> getCaseSAGtMap() {  //case -> posObs, val
        return Map.of(
                0,Pair.of(POS0,VAL0),
                1,Pair.of(POS1,VAL1),
                2,Pair.of(POS2,VAL2)

        );
    }


}
