package black_jack.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ReturnItem {
    public StateObserved state;
    public Double returnValue;
}
