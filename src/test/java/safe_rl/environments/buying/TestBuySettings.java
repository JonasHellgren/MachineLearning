package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import safe_rl.environments.buying_electricity.BuySettings;

public class TestBuySettings {

    BuySettings settings5 =BuySettings.new5HoursIncreasingPrice();
    BuySettings settings3 =BuySettings.new3HoursSamePrice();

    @Test
    void whenCreated5h_thenCorrectTimeEnd() {
        Assertions.assertEquals(4, settings5.timeEnd());
    }

    @Test
    void whenCreated3h_thenCorrectTimeEnd() {
        Assertions.assertEquals(2, settings3.timeEnd());
    }




}
