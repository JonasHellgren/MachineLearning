package mcts_spacegame.models_space;

import lombok.AllArgsConstructor;
import lombok.ToString;
import mcts_spacegame.environment.StepReturn;

@AllArgsConstructor
@ToString
public class State {
   public Integer x,y;

   public State copy () {
      return new State(x,y);
   }

   public void setFromReturn(StepReturn sr) {
      x=sr.newPosition.x;
      y=sr.newPosition.y;
   }
}
