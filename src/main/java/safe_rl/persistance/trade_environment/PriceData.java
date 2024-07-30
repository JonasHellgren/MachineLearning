package safe_rl.persistance.trade_environment;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.util.Pair;

import java.util.List;

public record PriceData(
        DayId id,
        ElType type,
        List<Double> pricesAllHours) {

    public static PriceData of(DayId id,
                               ElType type,
                               List<Double> pricesAllHours) {
        return new PriceData(id, type, pricesAllHours);
    }

    public List<Double> pricesFromHourToHourThisDay(Pair<Integer,Integer> fromToHour) {
        Integer fromHour = fromToHour.getFirst();
        Integer toHour = fromToHour.getSecond();
        checkHour(fromHour);
        checkHour(toHour);
        Preconditions.checkArgument(fromHour<=toHour,
                "from hour must be equal or smaller than to hour");
        return pricesAllHours.subList(fromHour, toHour);
    }

    public List<Double> prices00ToHour(int toHour) {
        checkHour(toHour);
        return pricesFromHourToHourThisDay(Pair.create(0, toHour));
    }

    public List<Double> pricesFromHourTo00(int fromHour) {
        checkHour(fromHour);
        return pricesFromHourToHourThisDay(Pair.create(fromHour, pricesAllHours.size()));
    }

    public double priceAtHour(int hour) {
        checkHour(hour);
        return pricesAllHours.get(hour);
    }

    private static void checkHour(int hour) {
        Preconditions.checkArgument(hour >= 0 && hour <= 24, "Non existing hour");
    }


}
