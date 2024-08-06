package safe_rl.persistance;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.persistance.trade_environment.*;

import java.util.List;

class TestElPriceXlsRead {

    final static String PATH = "src/main/java/safe_rl/persistance/data/";
    static final String REGION = "se3";
    static final DayId DAY0_JAN = DayId.of(24, 0, 0, REGION);
    PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "day-ahead-2024_EurPerMWh");
    PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "fcr-n-2024_EurPerMW");
    ElPriceXlsReader reader;
    ElPriceRepo repo;

    @BeforeEach
    void init() {
        repo = ElPriceRepo.empty();
        reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
    }

    @Test
    void whenReading_thenFilledRepo() {
        Assertions.assertTrue(repo.isOkRepoData());
        Assertions.assertNotEquals(0, repo.sizeEnergyDB());
    }

    @Test
    void whenReading_thenFirstDayInJanHasNextDay() {
        List<DayId> ids = repo.idsHasNextDay();
        Assertions.assertTrue(ids.contains(DAY0_JAN));
    }

    @Test
    void whenReading_thenDay9InJanHasNoNextDay() {
        List<DayId> ids = repo.idsHasNextDay();
        Assertions.assertFalse(ids.contains(DayId.of(24,0,9, REGION)));
    }

    @Test
    void whenDay0InJan_thenCorrectData() {
        var priceData=repo.getPriceDataForDay(DAY0_JAN, ElType.ENERGY);
        System.out.println("priceData = " + priceData);
        Assertions.assertTrue(priceData.pricesAllHours().contains(29.56));
    }

    @Test
    void whenDay0InJanFrom17To22_thenCorrectData() {
        var prices=repo.pricesFromHourToHour(
                DAY0_JAN,
                Pair.create(17,22),
                ElType.ENERGY);
        System.out.println("prices = " + prices);
        Assertions.assertTrue(prices.contains(64.99));
    }

    @Test
    void whenDay0InJanFrom17To08_thenCorrectData() {
        var prices=repo.pricesFromHourToHour(
                DAY0_JAN,
                Pair.create(17,8),
                ElType.ENERGY);
        System.out.println("prices = " + prices);
        Assertions.assertTrue(prices.contains(64.99));
        Assertions.assertTrue(prices.contains(32.03)); //day after
    }



}
