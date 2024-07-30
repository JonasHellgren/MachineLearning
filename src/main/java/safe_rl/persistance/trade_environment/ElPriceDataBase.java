package safe_rl.persistance.trade_environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElPriceDataBase implements DataBaseI<DayId,PriceData> {

    Map<DayId,PriceData> priceDataMap;

    public static ElPriceDataBase empty() {
        return new ElPriceDataBase();
    }

    private ElPriceDataBase() {
        priceDataMap=new HashMap<>();
    }

    @Override
    public void create(PriceData data) {
        priceDataMap.put(data.id(),data);
    }

    @Override
    public PriceData read(DayId id) {
        return priceDataMap.get(id);
    }

    @Override
    public boolean exists(DayId id) {
        return priceDataMap.containsKey(id);
    }

    @Override
    public List<DayId> getIds() {
        return new ArrayList<>(priceDataMap.keySet());
    }

    @Override
    public void clear() {
        priceDataMap.clear();
    }

    @Override
    public int size() {
        return priceDataMap.size();
    }
}
