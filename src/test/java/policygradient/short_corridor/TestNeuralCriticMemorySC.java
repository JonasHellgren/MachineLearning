package policygradient.short_corridor;

import common.RandUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.the_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.the_problems.short_corridor.NeuralCriticMemorySC;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import policy_gradient_problems.the_problems.short_corridor.VariablesSC;

import java.io.IOException;
import java.util.*;

import static org.knowm.xchart.BitmapEncoder.*;

public class TestNeuralCriticMemorySC {

    public static final double VAL0 = -0.5, VAL1=0.5, VAL2=VAL0;
    public static final double TOL = 0.2;
    public static final int NOF_FITS_PER_EPOCH = 1;
    public static final int NOF_CASES = 3;
    static NeuralCriticMemorySC critic;
    static Map<Integer,Double> svMapPrev;
    public static final String CHART_DIR = "src/test/java/policygradient/short_corridor/", FILE_NAME = "Value_Chart";


    @SneakyThrows
    @BeforeAll
    public static  void init() {
        critic=NeuralCriticMemorySC.newDefault();
        List<List<Double>> valuesList=trainCritic();
        printStateValues();

        plotValuesVersusEpisode(valuesList,List.of("0","1","2"));

    }

    @Test
    public void whenObsState0_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(0));
        Assertions.assertEquals(VAL0,value, TOL);
    }

    @Test
    public void whenObsState1_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(1));
        Assertions.assertEquals(VAL1,value, TOL);
    }


    @Test
    public void whenObsState2_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(2));
        Assertions.assertEquals(VAL2,value, TOL);
    }


    static List<List<Double>> trainCritic() {
        var caseSGtMap = getCaseSAGtMap();
        List<List<Double>> valuesList=new ArrayList<>();
        List<Double> values0=new ArrayList<>();
        List<Double> values1=new ArrayList<>();
        List<Double> values2=new ArrayList<>();

        for (int ei = 0; ei < 1000 ; ei++) {
            int caseNr= RandUtils.getRandomIntNumber(0, NOF_CASES);
            var pair=caseSGtMap.get(caseNr);
            var state= StateSC.newFromPos(pair.getLeft());
            List<Double> in = state.asList();
            List<Double> out = List.of(pair.getRight());
            critic.fit(List.of(in), out, NOF_FITS_PER_EPOCH);
            //valuesList.add(getStateValueMap().values().stream().toList());
            List<Double> values=getStateValueMap().values().stream().toList();
            values0.add(values.get(0));
            values1.add(values.get(1));
            values2.add(values.get(2));

          //  System.out.println("in = " + in+", out = " + out);
          //  printAllStateValues();
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
        saveBitmap(chart, CHART_DIR + FILE_NAME, BitmapEncoder.BitmapFormat.PNG);

    }

    private static void printStateValues() {
        Map<Integer, Pair<Integer, Double>> caseSAGtMap = getCaseSAGtMap();

        for (int ci = 0; ci < NOF_CASES ; ci++) {
            var pair=caseSAGtMap.get(ci);
            var estOut=critic.getOutValue(StateSC.newFromPos(pair.getLeft()));
            System.out.println("inData = " + pair+", estOut = " + estOut);
        }
    }


    private static void printAllStateValues() {
        System.out.println("policy");
        Map<Integer, Double> svMap = getStateValueMap();
        System.out.println("svMap = " + svMap);

        Map<Integer,Double> diffSvMap=new HashMap<>();
        if (!Objects.isNull(svMapPrev)) {
            for (int pos : svMap.keySet()) {
                diffSvMap.put(pos, svMap.get(pos) - svMapPrev.get(pos));
            }
            System.out.println("diffSvMap = " + diffSvMap);
        }
        svMapPrev=new HashMap<>(svMap);


    }

    @NotNull
    private static Map<Integer, Double> getStateValueMap() {
        Map<Integer,Double> svMap=new HashMap<>();
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; pos++) {
            StateI<VariablesSC> stateSC = StateSC.newFromPos(pos);
            svMap.put(pos,critic.getOutValue(stateSC));
        }
        return svMap;
    }

    private static Map<Integer, Pair<Integer, Double>> getCaseSAGtMap() {  //case -> pos, val
        return Map.of(
                0,Pair.of(0,VAL0),
                1,Pair.of(1,VAL1),
                2,Pair.of(2,VAL2)

        );
    }


}
