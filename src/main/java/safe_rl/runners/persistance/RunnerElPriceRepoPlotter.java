package safe_rl.runners.persistance;

import safe_rl.persistance.trade_environment.*;

public class RunnerElPriceRepoPlotter {

    static final String PATH = "src/main/java/safe_rl/persistance/data/";
   // static final String REGION = "se3";
    public static final ElPriceRepoPlotter.Settings NO_LEGEND_SETTINGS =
            ElPriceRepoPlotter.Settings.builder().isLegend(false).build();
    public static final ElPriceRepoPlotter.Settings DEF_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault();

    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "day-ahead-2024_EurPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "fcr-n-2024_EurPerMW");

    public static void main(String[] args) {

        ElPriceRepo repo = ElPriceRepo.empty();
        ElPriceXlsReader reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
        ElPriceRepoPlotter plotter= ElPriceRepoPlotter.withSeetings(repo, DEF_SETTINGS);
        plotter.plotTrajectories(ElType.ENERGY);
        plotter.plotTrajectories(ElType.FCR);

        plotter.plotScatter();


    }

}
