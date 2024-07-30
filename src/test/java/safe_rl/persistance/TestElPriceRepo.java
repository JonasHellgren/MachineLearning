package safe_rl.persistance;

import common.list_arrays.ListUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.persistance.trade_environment.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestElPriceRepo {

    static final String REGION = "SE3";
    static final int YEAR = 24;
    static final int N_DAYS_PER_DAY = YEAR;
    static final int MONTH = 0;
    static final DayId DAY_ID0 = DayId.of(YEAR, MONTH, 0, REGION);
    static final DayId DAY_ID1 = DayId.of(YEAR, MONTH, 1, REGION);
    final static List<Double> EP_TRAJ0 = ListUtils.doublesStartStepNitems(0, 1, N_DAYS_PER_DAY);
    final static List<Double> FCRP_TRAJ0 = ListUtils.doublesStartStepNitems(10, 10, N_DAYS_PER_DAY);
    final static List<Double> EP_TRAJ1 = ListUtils.doublesStartStepNitems(24, -1, N_DAYS_PER_DAY);
    final static List<Double> FCRP_TRAJ1 = ListUtils.doublesStartStepNitems(100, 100, N_DAYS_PER_DAY);

    ElPriceRepo repo;

    @BeforeEach
    void init() {
        repo = ElPriceRepo.empty();
        repo.addDataForDay(PriceData.of(DAY_ID0, ElType.ENERGY, EP_TRAJ0));  //day 0
        repo.addDataForDay(PriceData.of(DAY_ID0, ElType.FCR, FCRP_TRAJ0));  //day 0
        repo.addDataForDay(PriceData.of(DAY_ID1, ElType.ENERGY, EP_TRAJ1));  //day 1
        repo.addDataForDay(PriceData.of(DAY_ID1, ElType.FCR, FCRP_TRAJ1));  //day 1
    }

    @Test
    void checkIsOk() {
        Assertions.assertTrue(repo.check());
    }

    @Test
    void whenIdsAll_thenCorrect() {
        var ids=repo.idsAll();
        Assertions.assertEquals(2,ids.size());
        Assertions.assertTrue(ids.contains(DAY_ID0));
    }

    @Test
    void whenDay0_thenHasNextDay() {
        List<DayId> ids = repo.idsHasNextDay();
        Assertions.assertEquals(1,ids.size());
        Assertions.assertTrue(ids.contains(DAY_ID0));
    }

    @Test
    void whenDay0_thenCorrectPriceData() {
        PriceData data0=repo.getPriceDataForDay(DAY_ID0,ElType.ENERGY);
        Assertions.assertEquals(EP_TRAJ0,data0.pricesAllHours());
    }

    @Test
    void whenDay0AndFrom17To17_thenFail() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.pricesFromHourToHour(DAY_ID0, Pair.create(17,17),ElType.ENERGY)
        );
    }

    @Test
    void whenDay0AndFrom17To18_thenOnePriceFourHour17() {
        List<Double> prices=repo.pricesFromHourToHour(DAY_ID0, Pair.create(17,18),ElType.ENERGY);
        Assertions.assertEquals(17,prices.get(0));
    }

    @Test
    void whenDay0AndFrom17To20_thenCorrectPrices() {
        List<Double> prices=repo.pricesFromHourToHour(DAY_ID0, Pair.create(17,24),ElType.ENERGY);
        Assertions.assertEquals(ListUtils.doublesStartStepNitems(17, 1,24-17),prices);
    }

    @Test
    void whenDay0AndFrom17To08_thenCorrectNofPrices() {
        List<Double> prices=repo.pricesFromHourToHour(DAY_ID0, Pair.create(17,8),ElType.ENERGY);
        Assertions.assertEquals(24-17+8,prices.size());
    }


    @Test
    void whenDay1AndFrom17To08_thenThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.pricesFromHourToHour(DAY_ID1, Pair.create(17,8),ElType.ENERGY)
        );
    }


}
