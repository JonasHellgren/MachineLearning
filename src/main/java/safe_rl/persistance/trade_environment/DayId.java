package safe_rl.persistance.trade_environment;

public record DayId(
        int year,
        int month,
        int day,
        String region
) {
    
    public static DayId of(int year,
                                    int month,
                                    int day,
                                    String region) {
        return new DayId(year,month,day,region);
    }

    public DayId nextDay() {
        return DayId.of(year,month,day+1,region);
    }
    
}
