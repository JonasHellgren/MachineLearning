package safe_rl.persistance.trade_environment;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;
import common.other.Conditionals;
import org.apache.commons.math3.util.Pair;

import java.util.List;

/***
 *   Repository for storing energy (day-ahead) and FCR prices

 *  Logic for time windows:
 *  fromHour=17, toHour=20  => same day
 *  fromHour=17, toHour=8  =>  (8<17) toHour is at next day
 *
 */

public class ElPriceRepo {
    DataBaseI<DayId, PriceData> energyPriceDatabase;
    DataBaseI<DayId, PriceData> fcrPriceDatabase;
    DataBaseInformer informer;

    public static ElPriceRepo empty() {
        return new ElPriceRepo();
    }

    public ElPriceRepo() {
        energyPriceDatabase = ElPriceDataBase.empty();
        fcrPriceDatabase = ElPriceDataBase.empty();
        informer = DataBaseInformer.of(energyPriceDatabase);
    }

    public void addDataForDay(PriceData priceData) {
        Conditionals.executeOneOfTwo(priceData.type().equals(ElType.ENERGY),
                () -> energyPriceDatabase.create(priceData),
                () -> fcrPriceDatabase.create(priceData));
    }

    public void clear() {
        energyPriceDatabase.clear();
        fcrPriceDatabase.clear();
    }

    public boolean check() {
        return sizeEnergyDB() == sizeFcrDB();
    }

    public int sizeEnergyDB() {
        return energyPriceDatabase.size();
    }

    public int sizeFcrDB() {
        return fcrPriceDatabase.size();
    }

    public List<DayId> idsAll() {
        return energyPriceDatabase.getIds();
    }

    public List<DayId> idsHasNextDay() {
        return energyPriceDatabase.getIds().stream()
                .filter(id -> informer.isDatePresent(id.year(), id.month(), id.day() + 1))
                .toList();
    }

    public PriceData getPriceDataForDay(DayId id, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id)
                : fcrPriceDatabase.read(id);
    }

    public List<Double> pricesFromHourToHour(DayId id,
                                             Pair<Integer, Integer> fromToHour,
                                             ElType type) {
        Preconditions.checkArgument(!fromToHour.getFirst().equals(fromToHour.getSecond()),
                "Hours must differ");
        boolean isToHourAtSameDay = fromToHour.getSecond() > fromToHour.getFirst();
        return (isToHourAtSameDay)
                ? pricesSameDayFromHourToHour(id, fromToHour, type)
                : pricesAlsoFromNextDay(id, fromToHour, type);

    }

    private List<Double> pricesSameDayFromHourToHour(DayId id, Pair<Integer, Integer> fromToHour, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id).pricesFromHourToHourThisDay(fromToHour)
                : fcrPriceDatabase.read(id).pricesFromHourToHourThisDay(fromToHour);
    }

    private List<Double> pricesAlsoFromNextDay(DayId id, Pair<Integer, Integer> fromToHour, ElType type) {
        Preconditions.checkArgument(informer.hasNextDay(id), "Day has no next day, dayId=" + id);
        List<Double> pricesTo00 = pricesFromHour(id, fromToHour.getFirst(), type);
        List<Double> pricesToHour = pricesToHour(id.nextDay(), fromToHour.getSecond(), type);
        var pricesAll= Lists.newArrayList(pricesTo00);
        pricesAll.addAll(pricesToHour);
        return pricesAll;
    }

    private List<Double> pricesFromHour(DayId id, int hour, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id).pricesFromHourTo00(hour)
                : fcrPriceDatabase.read(id).pricesFromHourTo00(hour);
    }

    private List<Double> pricesToHour(DayId id, int hour, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id).prices00ToHour(hour)
                : fcrPriceDatabase.read(id).prices00ToHour(hour);
    }

}
