package safe_rl.runners.persistance;

import safe_rl.persistance.trade_environment.*;

public class RunnerElPriceRepoPlotter {

    static final String PATH = "src/main/java/safe_rl/persistance/data/";
    static final String REGION = "se3";
    static final DayId DAY0_JAN = DayId.of(24, 0, 0, REGION);
    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "day-ahead-2024_EurPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "fcr-n-2024_EurPerMW");

    public static void main(String[] args) {

        ElPriceRepo repo = ElPriceRepo.empty();
        ElPriceXlsReader reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
        ElPriceRepoPlotter plotter=new ElPriceRepoPlotter(repo);
        plotter.plotTrajectories(ElType.ENERGY);
        plotter.plotTrajectories(ElType.FCR);


    }

}
