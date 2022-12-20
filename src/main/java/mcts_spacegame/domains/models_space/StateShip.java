package mcts_spacegame.domains.models_space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.classes.StepReturnGeneric;

@AllArgsConstructor
@ToString
@Getter
public class StateShip implements StateInterface<ShipVariables> {
   ShipVariables variables;


   public static StateShip newStateFromXY(int x, int y) {
      return new StateShip(ShipVariables.builder().x(x).y(y).build());
   }

   public static StateShip newWithVariables(ShipVariables variables) {
      return new StateShip(variables);
   }

   public int getX() {
      return variables.x;
   }

   public int getY() {
      return variables.y;
   }

   public StateShip copy () {
      return newWithVariables(variables.copy());
   }

   @Override
   public void setFromReturn(StepReturnGeneric<ShipVariables> stepReturn) {
      variables=stepReturn.copyState().getVariables();
   }

}
