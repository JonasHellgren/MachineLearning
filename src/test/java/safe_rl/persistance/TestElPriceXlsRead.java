package safe_rl.persistance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.persistance.trade_environment.ElPriceRepo;
import safe_rl.persistance.trade_environment.ElPriceXlsReader;
import safe_rl.persistance.trade_environment.ElType;
import safe_rl.persistance.trade_environment.PathAndFile;

class TestElPriceXlsRead {

    final static String PATH="src/main/java/safe_rl/persistance/data/";
    PathAndFile fileEnergy=PathAndFile.xlsxOf(PATH,"day-ahead-2024_EurPerMWh");
    PathAndFile fileFcr=PathAndFile.xlsxOf(PATH,"fcr-n-2024_EurPerMW");
    ElPriceXlsReader reader;
    ElPriceRepo repo;

    @BeforeEach
    void init() {
        repo=ElPriceRepo.empty();
        reader=ElPriceXlsReader.of(repo);
    }

    @Test
    void whenReading_thenFilledRepo() {
        System.out.println("fileEnergy.fullName() = " + fileEnergy.fullName());
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
        System.out.println("repo.sizeEnergyDB() = " + repo.sizeEnergyDB());
        Assertions.assertTrue(true);
    }

}
