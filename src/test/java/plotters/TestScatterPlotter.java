package plotters;

import org.apache.commons.lang3.tuple.Pair;
import plotters.PlotterScatter;

import java.util.List;

public class TestScatterPlotter {

    public static void main(String[] args) {
        List<Pair<Double,Double>> dataPairs=List.of(Pair.of(1d,-1d),Pair.of(2d,2d),Pair.of(3d,3d));
        PlotterScatter plotter=new PlotterScatter("posReal","value");
        plotter.plot(dataPairs);
    }
}
