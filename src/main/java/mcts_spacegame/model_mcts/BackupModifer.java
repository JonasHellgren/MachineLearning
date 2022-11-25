package mcts_spacegame.model_mcts;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path is:
 *   1) non fail and terminal => backup all nodes in path  (simulation ends in non-fail state)
 *  *                            backup as 3)              (simulation ends in fail state)
 *   2) non terminal =>  backup all nodes in path  (simulation ends in non-fail state)
 *                       backup as 3)              (simulation ends in fail state)
 *   3) fail and terminal => backup parent of end node AND
 *   every node in path who's children's all are fail and terminal
 *
 *   simulation not applicable for case 3)
 */

public class BackupModifer {



}
