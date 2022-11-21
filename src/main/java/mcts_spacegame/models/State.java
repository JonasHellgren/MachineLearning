package mcts_spacegame.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class State {
   public Integer x,y;

   public State copy () {
      return new State(x,y);
   }
}
