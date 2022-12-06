package mcts_spacegame.model_mcts;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken

 *    normal backup = use alphaNormal and discountFactorSimulationNormal
 *    defensive backup = use alphaDefensive and discountFactorSimulationDefensive
 *
 *   a single simulation:
 *   1) all simulations are terminal-non fail => defensive backup
 *   2) at least one simulation is terminal-non fail => normal backup from mix of max and average of non-fail simulations
 *
 *
 */
public class BackupModifierFromSimulations {




}
