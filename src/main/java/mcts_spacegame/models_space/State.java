package mcts_spacegame.models_space;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mcts_spacegame.environment.StepReturn;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class State {
   public Integer x,y;

   public static State newState(int x, int y) {
      return new State(x,y);
   }

   public State copy () {
      return new State(x,y);
   }

   public void setFromReturn(StepReturn sr) {
      x=sr.newPosition.x;
      y=sr.newPosition.y;
   }
}
