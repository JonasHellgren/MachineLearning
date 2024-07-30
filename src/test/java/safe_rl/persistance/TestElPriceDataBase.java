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

public class TestElPriceDataBase {

    public static final String REGION = "SE3";
    public static final int YEAR = 24;
    ElPriceDataBase dataBase;
    public static final int N_DAYS_PER_DAY = YEAR;
    final static List<Double> EP_TRAJ0 =ListUtils.doublesStartStepNitems(0,1, N_DAYS_PER_DAY);
    final static List<Double> EP_TRAJ1 =ListUtils.doublesStartStepNitems(1,1, N_DAYS_PER_DAY);


    @BeforeEach
    void init() {
        dataBase=ElPriceDataBase.empty();
        dataBase.create(PriceData.of(DayId.of(YEAR,0, REGION), ElType.ENERGY, EP_TRAJ0));
        dataBase.create(PriceData.of(DayId.of(YEAR,1, REGION), ElType.ENERGY, EP_TRAJ1));
    }

    @Test
    void whenCreated_thenCorrectSize() {
        Assertions.assertEquals(2,dataBase.size());
    }

    @Test
    void whenCleared_thenCorrectSize() {
        dataBase.clear();
        Assertions.assertEquals(0,dataBase.size());
    }

    @Test
    void whenCreated_thenDataExists() {
        IntStream.range(0,2).forEach(dayi ->
                Assertions.assertTrue(dataBase.exists(DayId.of(YEAR,dayi, REGION))));
    }

    @Test
    void whenCreated_thenDataDayIndex2NotExists() {
      Assertions.assertFalse(dataBase.exists(DayId.of(YEAR,2, REGION)));
    }

    @Test
    void whenCreated_thenCorrectIds() {
        List<DayId> ids=dataBase.getIds();
        Assertions.assertTrue(ids.containsAll(List.of(DayId.of(YEAR,0, REGION),DayId.of(YEAR,0, REGION))));
    }

    @Test
    void whenReading_thenCorrectPriceAtHour0() {
        var pd=dataBase.read(DayId.of(YEAR,0, REGION));
        Assertions.assertEquals(0,pd.priceAtHour(0));
    }

    @Test
    void whenReading_thenCorrectToPrices() {
        var pd=dataBase.read(DayId.of(YEAR,0, REGION));
        int toHourExcluded = 8;
        List<Double> prices00To5 = pd.prices00ToHour(toHourExcluded);
        Assertions.assertEquals(ListUtils.doublesStartStepNitems(0,1, toHourExcluded), prices00To5);
        Assertions.assertEquals(8, prices00To5.size());
    }

    @Test
    void whenReading_thenCorrectFromPrices() {
        var pd=dataBase.read(DayId.of(YEAR,0, REGION));
        int fromHour = 17;
        List<Double> pricesfrom17to00 = pd.pricesHourTo00(fromHour);
        Assertions.assertEquals(ListUtils.doublesStartStepNitems(fromHour,1, N_DAYS_PER_DAY-17), pricesfrom17to00);
        Assertions.assertEquals(N_DAYS_PER_DAY-17, pricesfrom17to00.size());
    }


}
