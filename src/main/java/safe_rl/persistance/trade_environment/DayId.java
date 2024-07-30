package safe_rl.persistance.trade_environment;

import lombok.Builder;

public record DayId(
        int year,
        int month,
        String region
) {
    
    public static DayId of(int year,
                                    int month,
                                    String region) {
        return new DayId(year,month,region);
    }
    
}
