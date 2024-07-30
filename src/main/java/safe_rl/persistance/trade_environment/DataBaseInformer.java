package safe_rl.persistance.trade_environment;

import lombok.AllArgsConstructor;

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

}
