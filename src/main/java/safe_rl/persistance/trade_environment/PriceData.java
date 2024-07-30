package safe_rl.persistance.trade_environment;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.util.List;

@Getter
public record PriceData(
        DayId id,
        ElType type,
        List<Double> pricesAllHours) {


    public List<Double> prices00ToHour(int toHour) {
        checkHour(toHour);
        return pricesAllHours.subList(0, toHour);
    }

    public List<Double> pricesHourTo00(int fromHour) {
        checkHour(fromHour);
        return pricesAllHours.subList(fromHour, pricesAllHours.size());
    }

    public double priceAtHour(int hour) {
        checkHour(hour);
        return pricesAllHours.get(hour);
    }

    private static void checkHour(int hour) {
        Preconditions.checkArgument(hour >= 0 && hour < 24, "Non existing hour");
    }


}
