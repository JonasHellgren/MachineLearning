package safe_rl.runners.persistance;

import org.apache.commons.math3.util.Pair;
import safe_rl.persistance.trade_environment.*;

import java.util.List;

public class RunnerElPriceRepoPlotter {

    static final String PATH = "src/main/java/safe_rl/persistance/data/";
    public static final ElPriceRepoPlotter.Settings NO_LEGEND_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault().withIsLegend(false);
    public static final ElPriceRepoPlotter.Settings DEF_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault();
    public static final int N_CLUSTERS = 4;


/*
    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "day-ahead-2024_EurPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "fcr-n-2024_EurPerMW");
*/


    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "Day-ahead-6months-EuroPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "FCR-N-6months-EuroPerMW");


    public static void main(String[] args) {
        ElPriceRepo repo = ElPriceRepo.empty();
        ElPriceXlsReader reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
        repo.throwIfNotOkRepoData();
        ElPriceRepoPlotter plotter= ElPriceRepoPlotter.withSettings(repo, DEF_SETTINGS);
        plotter.plotTrajectories(ElType.ENERGY);
        plotter.plotTrajectories(ElType.FCR);
        plotter.plotScatter();
        var xy = getAddedPoints(repo);
        plotter.plotScatterWithAddedPoints(xy.getFirst(),xy.getSecond());
        printDaysForAddedPoints(repo, xy);
    }

    private static void printDaysForAddedPoints(ElPriceRepo repo, Pair<List<Double>, List<Double>> xy) {
        var informer=new RepoInformer(repo);
        var stdList= xy.getSecond();
        stdList.forEach(stdPrice -> {
            DayId id = informer.findDayWithEqualStdPrice(stdPrice, ElType.ENERGY)
                    .orElseThrow(() -> new RuntimeException("DayId not found for stdPrice: " + stdPrice));
            System.out.println("stdPrice=" + stdPrice + ", id = " + id);
        });
    }

    private static Pair<List<Double>, List<Double>> getAddedPoints(ElPriceRepo repo) {
        var informer=new RepoInformer(repo);
        var avgList=informer.averagePriceEachDay(ElType.FCR);
        var stdList=informer.stdPriceEachDay(ElType.ENERGY);
        var finder= ClosestPointToClusterCenterFinder.builder()
                        .xValues(avgList).yValues(stdList).nClusters(N_CLUSTERS).build();
        return finder.find();
    }


}
