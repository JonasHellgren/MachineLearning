package mcts_spacegame.models_space;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mcts_spacegame.environment.StepReturn;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class State {
   ShipVariables variables;


   public static State newState(int x, int y) {
      return new State(ShipVariables.builder().x(x).y(y).build());
   }

   public static State newWithVariables(ShipVariables variables) {
      return new State(variables);
   }


   public State copy () {
      return newWithVariables(variables.copy());
   }

   public void setFromReturn(StepReturn sr) {
      //todo
 //     x=sr.newPosition.x;
   //   y=sr.newPosition.y;
   }
}
