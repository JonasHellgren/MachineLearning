package safe_rl.persistance.trade_environment;

import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DataBaseInformer {

    DataBaseI<DayId, PriceData> dataBase;

    public static DataBaseInformer of(DataBaseI<DayId, PriceData> dataBase) {
        return new DataBaseInformer(dataBase);
    }

  public  boolean isDatePresent(int year, int month, int day) {
      return dataBase.getIds().stream()
              .anyMatch(id -> id.year()==year && id.month()==month && id.day()==day);
  }

    public  boolean hasNextDay(DayId id) {
        return isDatePresent(id.year(), id.month(), id.day() + 1);
    }

    public double maxPrice() {
        return ListUtils.findMax(getAllPricesAllDays()).orElseThrow();
    }

    public double minPrice() {
        return ListUtils.findMin(getAllPricesAllDays()).orElseThrow();
    }

    List<Double> getAllPricesAllDays() {
        return dataBase.getIds().stream()
                .flatMap(id -> dataBase.read(id).pricesAllHours().stream())
                .toList();
    }

}
