package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import safe_rl.environments.buying_electricity.BuySettings;

public class TestBuySettings {

    BuySettings settings=BuySettings.new5HoursIncreasingPrice();


    @Test
    void whenCreated_thenCorrectTimeEnd() {
        Assertions.assertEquals(4,settings.timeEnd());
    }


}
