package monte_carlo_tree_search.domains.models_space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.classes.StepReturnGeneric;

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
