package safe_rl.persistance.trade_environment;

import java.util.HashMap;
import java.util.Map;

public record SheetNameDecoder(
        String sheetName
) {


    public static final int POS_YEAR = 0;
    public static final int POS_MONTH = 1;
    public static final int POS_REGION = 2;

    public int year() {
        return  Integer.parseInt(splitName()[POS_YEAR]);
    }

    public int month() {
        var monthStr=splitName()[POS_MONTH];
        var map=monthMap();
        Integer month = map.get(monthStr);

        if (month == null) {
            throw new IllegalArgumentException("Invalid month: " + monthStr);
        }

        return month;
    }

    public String region() {
        return  splitName()[POS_REGION];
    }

    String[] splitName() {
        if (sheetName.chars().filter(ch -> ch == '-').count() != 2) {
            throw new IllegalArgumentException("Sheet name format is incorrect: expected exactly two - (year-month-region)");
        }
        return  sheetName.split("-");
    }

    Map<String,Integer> monthMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("jan", 0);
        map.put("feb", 1);
        map.put("mar", 2);
        map.put("apr", 3);
        map.put("may", 4);
        map.put("jun", 5);
        map.put("jul", 6);
        map.put("aug", 7);
        map.put("sep", 8);
        map.put("oct", 9);
        map.put("nov", 10);
        map.put("dec", 11);
        return map;
    }

    public @Override String toString() {
        return "year="+year()+", month="+month()+", region="+region();
    }
}
