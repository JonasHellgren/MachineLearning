package safe_rl.persistance.trade_environment;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import common.other.Conditionals;

import java.util.List;

public class ElPriceRepo {
    DataBaseI<DayId, PriceData> energyPriceDatabase;
    DataBaseI<DayId, PriceData> fcrPriceDatabase;
    DataBaseInformer informer;

    public ElPriceRepo() {
        energyPriceDatabase = ElPriceDataBase.empty();
        fcrPriceDatabase = ElPriceDataBase.empty();
        informer=DataBaseInformer.of(energyPriceDatabase);
    }

    public boolean check() {
        return energyPriceDatabase.size()== fcrPriceDatabase.size();
    }

    public List<DayId> getAllIds() {
        return energyPriceDatabase.getIds();
    }

    public List<DayId> getIdsHasNextDay() {
        return energyPriceDatabase.getIds().stream()
                .filter(id -> informer.isMonthAndDayPresent(id.year(), id.month(), id.day()+1))
                .toList();
    }

    public PriceData getPriceDataForDay(DayId id, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id)
                : fcrPriceDatabase.read(id);
    }


    public List<Double> pricesFromHourToHour(DayId id,
                                             Range<Integer> fromToHour,
                                             ElType type) {
        Preconditions.checkArgument(informer.hasNextDay(id),"Day has no next day, dayId="+id);
        List<Double> pricesTo00=getPricesForDayToFromHour(id, fromToHour.lowerEndpoint(), type);
        List<Double> pricesToHour=getPricesForDayToHour(id, fromToHour.upperEndpoint(), type);
        pricesTo00.addAll(pricesToHour);
        return pricesTo00;
    }

    public void addDataForDay(PriceData priceData, ElType type) {
        Conditionals.executeOneOfTwo(type.equals(ElType.ENERGY),
                () -> energyPriceDatabase.create(priceData),
                () -> fcrPriceDatabase.create(priceData));
    }


    public List<Double> getPricesForDayToFromHour(DayId id, int hour, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id).pricesFromHourTo00(hour)
                : fcrPriceDatabase.read(id).pricesFromHourTo00(hour);
    }

    public List<Double> getPricesForDayToHour(DayId id, int hour, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyPriceDatabase.read(id).prices00ToHour(hour)
                : fcrPriceDatabase.read(id).prices00ToHour(hour);
    }




}
