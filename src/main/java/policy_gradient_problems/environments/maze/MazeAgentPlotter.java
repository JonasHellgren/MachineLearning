package policy_gradient_problems.environments.maze;

import common.plotters.SwingShowHeatMap;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.AnnotationText;
import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.HeatMapChartBuilder;
import org.knowm.xchart.SwingWrapper;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static common.ArrayUtil.transposeMatrix;

@AllArgsConstructor
public class MazeAgentPlotter {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;
    AgentNeuralActorNeuralCriticI<VariablesMaze> agent;
    MazeSettings settings;
    final SwingShowHeatMap valueMapPlotter =SwingShowHeatMap.builder()
            .width(WIDTH).height(HEIGHT)
            .build();

    public void plotValues() {
        double[][] data = getValueData();
        valueMapPlotter.showMap(data,"");
    }


    public void plotBestAction() {
        HeatMapChart chart = createChart();
        addData(chart,transposeMatrix(new int[settings.gridHeight()][settings.gridWidth()]));
        String[][] data = getBestActionData();
        System.out.println("data = " + Arrays.deepToString(data));
        addCellText(chart, data);
        new SwingWrapper<>(chart).displayChart();
    }


    private double[][] getValueData() {
        double[][] valMat = new double[settings.gridHeight()][settings.gridWidth()];
        for (int y = 0; y < settings.gridHeight(); y++) {
            for (int x = 0; x < settings.gridWidth(); x++) {
            valMat[y][x]=agent.criticOut(StateMaze.newFromPoint(new Point2D.Double(x,y)));
            }
        }
        return valMat;
    }

    private String[][] getBestActionData() {
        String[][] aMat = new String[settings.gridHeight()][settings.gridWidth()];
        Map<Integer,String> actions= Map.of(0,"u",1,"r",2,"d",3,"l");
        for (int y = 0; y < settings.gridHeight(); y++) {
            for (int x = 0; x < settings.gridWidth(); x++) {
                agent.setState(StateMaze.newFromPoint(new Point2D.Double(x,y)));
                var probs=agent.actionProbabilitiesInPresentState();
                OptionalInt indexOpt = IntStream.range(0, probs.size())
                        .reduce((i, j) -> probs.get(i) < probs.get(j) ? j : i);
                aMat[y][x]=actions.get(indexOpt.orElseThrow());
            }
        }
        return aMat;
    }


    @NotNull
    private static HeatMapChart createChart() {
        var chart = new HeatMapChartBuilder()
                .title("Sample HeatMap")
                .xAxisTitle("X Axis").yAxisTitle("Y Axis")
                .width(WIDTH).height(HEIGHT)
                .build();
        chart.getStyler().setChartTitleVisible(true).setLegendVisible(false);
        chart.getStyler().setMin(0).setMax(100).setRangeColors(new Color[]{Color.LIGHT_GRAY,Color.WHITE});
        chart.getStyler().setAnnotationTextFontColor(Color.BLACK);
        chart.getStyler().setAnnotationTextFont(new Font("Arial", Font.BOLD, 22));
        return chart;
    }

    private  void addData(HeatMapChart chart, int[][] data) {
        int[] xData = IntStream.range(0, settings.gridWidth() ).toArray();
        int[] yData=IntStream.range(0, settings.gridHeight()).toArray();
        chart.addSeries("a", xData, yData, data);
    }

    private static void addCellText(org.knowm.xchart.HeatMapChart chart, String[][] data) {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                String text = data[y][x];
                AnnotationText annotation = new AnnotationText(text, x,y, false);
                chart.addAnnotation(annotation);
            }
        }
    }

}
