package monte_carlo_tree_search.domains.elevator;

/**
 *  Influenced by https://busoniu.net/files/papers/ifac08-elevators.pdf
 *
 *
 *    |_30__|____ floor 3
 *    |_29__|
 *     :
 *     :
 *    |_21__|
 *    |_20__|____ floor 2
 *    |_19__|
 *     :
 *     :
 *    |_11__|
 *    |_10__|____ floor 1
 *    |_09__|
 *     :
 *     :
 *    |_01__|
 *    |_00__|____ floor 0
 *
 *   states={pos, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 *
 *   action = speed
 *
 *   d/dt pos= speed
 *   d/dt SoE=  powerCharge  (pos == 0)
 *              powerUp  (speed is positive)
 *              powerDown (speed is negative)
                0 (speed is zero)
 *
 *   nPersonsInElevator = 0 if floor = 0, increased if entering floor with waiting person(s)
 *   nPersonsWaitingFloor i = zero if pos corresponds to that floor
 *
 *   reward=sum (nPersonsWaitingFloor i)
 *
 *
 *
 */


public class EnvironmentElevator {
}
