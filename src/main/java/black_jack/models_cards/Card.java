package black_jack.models_cards;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 1 = Ace, 2-10 = Number cards, Jack/Queen/King = 10
 */

@ToString
@Getter
public class Card {
  long value;

  public static Card newRandom() {
      return new Card();
  }

    public static Card newWithValue(long value) {
        return new Card(value);
    }

    private Card() {
        setRandomValue();
    }

    private Card(long value) {
        this.value = value;
    }

    private void setRandomValue() {
     List<Integer> deck = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10);
     value=getRandomItemFromIntegerList(deck);
  }

    private static Integer getRandomItemFromIntegerList(List<Integer> list) {
        Random random=new Random();
        return list.get(random.nextInt(list.size()));
    }

}
