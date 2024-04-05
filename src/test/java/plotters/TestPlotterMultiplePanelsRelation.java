package plotters;

import common.plotters.PlotterMultiplePanelsPairs;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TestPlotterMultiplePanelsRelation {

    public static void main(String[] args) {
        List<Pair<Double,Double>> dataPairs1=List.of(Pair.of(1d,-1d),Pair.of(2d,2d),Pair.of(3d,3d));
        List<Pair<Double,Double>> dataPairs2=List.of(Pair.of(1d,1d),Pair.of(3d,2d),Pair.of(30d,3d));

        List<List<Pair<Double,Double>>> listOfDataPairs0=List.of(dataPairs1);
        PlotterMultiplePanelsPairs plotter0=new PlotterMultiplePanelsPairs("posReal","value");
        plotter0.plot(listOfDataPairs0);

        List<List<Pair<Double,Double>>> listOfDataPairs=List.of(dataPairs1,dataPairs2);
        PlotterMultiplePanelsPairs plotter1=new PlotterMultiplePanelsPairs("posReal","value");
        plotter1.plot(listOfDataPairs);

        PlotterMultiplePanelsPairs plotter2=new PlotterMultiplePanelsPairs(
                PlotterMultiplePanelsPairs.Settings.builder()
                        .width(300).height(200).titleList(List.of("A","B"))
                        .xLabelList(List.of("A","B")).yLabelList(List.of("A","B"))
                        .build()
        );
        plotter2.plot(listOfDataPairs);

    }
}
