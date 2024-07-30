package safe_rl.persistance;

import common.list_arrays.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.persistance.trade_environment.DayId;
import safe_rl.persistance.trade_environment.ElPriceDataBase;
import safe_rl.persistance.trade_environment.ElType;
import safe_rl.persistance.trade_environment.PriceData;

import java.util.List;
import java.util.stream.IntStream;

class TestElPriceDataBase {

    static final String REGION = "SE3";
    static final int YEAR = 24;
    static final int N_DAYS_PER_DAY = YEAR;
    static final int MONTH = 0;
    static final DayId DAY_ID1 = DayId.of(YEAR, MONTH, 1, REGION);
    static final DayId DAY_ID0 = DayId.of(YEAR, MONTH, 0, REGION);
    final static List<Double> EP_TRAJ0 = ListUtils.doublesStartStepNitems(0, 1, N_DAYS_PER_DAY);
    final static List<Double> EP_TRAJ1 = ListUtils.doublesStartStepNitems(10, 1, N_DAYS_PER_DAY);
    ElPriceDataBase dataBase;


    @BeforeEach
    void init() {
        dataBase = ElPriceDataBase.empty();
        dataBase.create(PriceData.of(DAY_ID0, ElType.ENERGY, EP_TRAJ0));
        dataBase.create(PriceData.of(DAY_ID1, ElType.ENERGY, EP_TRAJ1));
    }

    @Test
    void whenCreated_thenCorrectSize() {
        Assertions.assertEquals(2, dataBase.size());
    }

    @Test
    void whenCleared_thenCorrectSize() {
        dataBase.clear();
        Assertions.assertEquals(MONTH, dataBase.size());
    }

    @Test
    void whenCreated_thenDataExists() {
        IntStream.range(MONTH, 2).forEach(dayi ->
                Assertions.assertTrue(dataBase.exists(DayId.of(YEAR, MONTH, dayi, REGION))));
    }

    @Test
    void whenCreated_thenDataDayIndex2NotExists() {
        Assertions.assertFalse(dataBase.exists(DayId.of(YEAR, MONTH, 2, REGION)));
    }

    @Test
    void whenCreated_thenCorrectIds() {
        List<DayId> ids = dataBase.getIds();
        Assertions.assertTrue(ids.containsAll(List.of(DAY_ID0, DAY_ID1)));
    }

    @Test
    void whenReading_thenCorrectPriceAtHour0() {
        var pd = dataBase.read(DAY_ID0);
        Assertions.assertEquals(0, pd.priceAtHour(MONTH));
    }

    @Test
    void whenReading_thenCorrectToPrices() {
        var pd = dataBase.read(DAY_ID0);
        int toHourExcluded = 8;
        List<Double> prices00To5 = pd.prices00ToHour(toHourExcluded);
        Assertions.assertEquals(
                ListUtils.doublesStartStepNitems(MONTH, 1, toHourExcluded),
                prices00To5);
        Assertions.assertEquals(8, prices00To5.size());
    }

    @Test
    void whenReading_thenCorrectFromPrices() {
        var pd = dataBase.read(DAY_ID0);
        int fromHour = 17;
        List<Double> pricesfrom17to00 = pd.pricesFromHourTo00(fromHour);
        Assertions.assertEquals(
                ListUtils.doublesStartStepNitems(fromHour, 1, N_DAYS_PER_DAY - 17),
                pricesfrom17to00);
        Assertions.assertEquals(N_DAYS_PER_DAY - 17, pricesfrom17to00.size());
    }


}
